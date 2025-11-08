package com.example.shop.config;

import com.example.shop.repo.UserRepo;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.CookieClearingLogoutHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.security.SecureRandom;

@Configuration
public class SecurityConfig {

    private final UserRepo users;

    public SecurityConfig(UserRepo users) {
        this.users = users;
    }

    // 🔹 Benutzer aus DB laden
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> users.findByEmail(username)
                .map(u -> {
                    String role = (u.getRole() == null) ? "USER" : u.getRole();
                    UserDetails ud = org.springframework.security.core.userdetails.User
                            .withUsername(u.getEmail())
                            .password(u.getPasswordHash())
                            .roles(role)
                            .build();
                    return ud;
                })
                .orElseThrow(() -> new UsernameNotFoundException("Benutzer nicht gefunden: " + username));
    }

    // 🔹 Passwort-Encoder
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12, new SecureRandom());
    }

    // 🔹 Haupt-Sicherheitskonfiguration
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .authorizeHttpRequests(auth -> auth
                        // Öffentliche Seiten + Healthcheck + Assets
                        .requestMatchers(
                                "/maintenance", "/maintenance/**",
                                "/error", "/error/**",
                                "/actuator/health", "/actuator/health/**",
                                "/health", "/actuator/**",
                                "/", "/impressum", "/datenschutz",
                                "/register", "/verify", "/verify/resend",
                                "/login", "/logout",
                                "/favicon.ico", "/robots.txt", "/sitemap.xml",
                                "/css/**", "/js/**", "/img/**", "/images/**",
                                "/assets/**", "/fonts/**", "/webjars/**"
                        ).permitAll()

                        // Geschützte Bereiche
                        .requestMatchers("/cart/**", "/checkout/**", "/orders/**")
                        .hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        // Alles andere muss authentifiziert sein
                        .anyRequest().authenticated()
                )

                // Login / Logout
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/", true)
                        .failureUrl("/login?error")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "POST"))
                        .logoutSuccessUrl("/login?logout")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .addLogoutHandler(new CookieClearingLogoutHandler("JSESSIONID"))
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )

                // Fehlerseiten
                .exceptionHandling(ex -> ex.accessDeniedPage("/access-denied"))

                // CSRF – nur H2-Konsole ausnehmen
                .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**", "/actuator/**"))

                // Session-Schutz
                .sessionManagement(sm -> sm
                        .sessionFixation(session -> session.migrateSession())
                        .maximumSessions(3)
                        .maxSessionsPreventsLogin(false)
                )

                // Sicherheitsheader
                .headers(headers -> headers
                        .frameOptions(f -> f.sameOrigin())
                        .contentSecurityPolicy(csp -> csp
                                .policyDirectives(
                                        "default-src 'self'; " +
                                                "script-src 'self'; " +
                                                "style-src 'self' 'unsafe-inline'; " +
                                                "img-src 'self' data:; " +
                                                "object-src 'none'; " +
                                                "base-uri 'self'; " +
                                                "frame-ancestors 'none';"
                                )
                        )
                )

                .requestCache(Customizer.withDefaults());

        return http.build();
    }
}