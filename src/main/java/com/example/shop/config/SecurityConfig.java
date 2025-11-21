package com.example.shop.config;

import com.example.shop.repo.UserRepo;
import com.example.shop.security.RegistrationRateLimitFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.CookieClearingLogoutHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.security.SecureRandom;

@Configuration
public class SecurityConfig {

    private final UserRepo users;

    public SecurityConfig(UserRepo users) {
        this.users = users;
    }

    // 🔹 Benutzer aus DB laden + nur verifizierte E-Mails zulassen
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            String normalized = username == null ? "" : username.trim().toLowerCase();

            var user = users.findByEmailIgnoreCase(normalized)
                    .orElseThrow(() -> new UsernameNotFoundException("Unbekannter Benutzer"));

            if (!user.isEmailVerified()) {
                throw new UsernameNotFoundException("E-Mail ist noch nicht verifiziert");
            }

            String role = (user.getRole() == null || user.getRole().isBlank())
                    ? "USER"
                    : user.getRole();

            UserDetails ud = org.springframework.security.core.userdetails.User
                    .withUsername(user.getEmail())
                    .password(user.getPasswordHash())
                    .roles(role)
                    .build();

            return ud;
        };
    }

    // 🔹 Passwort-Encoder
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12, new SecureRandom());
    }

    // 🔹 SessionRegistry
    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    // 🔹 Haupt-Sicherheitskonfiguration
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   SessionRegistry sessionRegistry,
                                                   RegistrationRateLimitFilter registrationRateLimitFilter) throws Exception {

        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/maintenance", "/maintenance/**", "/error", "/error/**",
                                "/actuator/health", "/actuator/health/**",
                                "/health", "/actuator/**",
                                "/", "/impressum", "/datenschutz",
                                "/register", "/verify", "/verify/resend",
                                "/login", "/logout",
                                "/favicon.ico", "/robots.txt", "/sitemap.xml",
                                "/css/**", "/js/**", "/products/**", "/img/**", "/media/**", "/images/**",
                                "/assets/**", "/fonts/**", "/webjars/**"
                        ).permitAll()
                        .requestMatchers("/cart/**", "/checkout/**", "/orders/**")
                        .hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
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
                .exceptionHandling(ex -> ex.accessDeniedPage("/access-denied"))
                .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**", "/actuator/**"))
                .sessionManagement(sm -> sm
                        .sessionFixation(session -> session.migrateSession())
                        .maximumSessions(3)
                        .maxSessionsPreventsLogin(false)
                        .sessionRegistry(sessionRegistry)
                )
                .headers(headers -> headers
                        .frameOptions(f -> f.sameOrigin())
                        .contentSecurityPolicy(csp -> csp
                                .policyDirectives(
                                        "default-src 'self'; " +
                                                // reCAPTCHA Scripts erlauben
                                                "script-src 'self' https://www.google.com/recaptcha/ https://www.gstatic.com/recaptcha/; " +
                                                // Styles (dein eigenes CSS + inline, weil du viel mit Klassen arbeitest)
                                                "style-src 'self' 'unsafe-inline'; " +
                                                // Bilder inkl. reCAPTCHA
                                                "img-src 'self' data: https://www.google.com/recaptcha/; " +
                                                // Frames für reCAPTCHA
                                                "frame-src 'self' https://www.google.com/recaptcha/; " +
                                                // optional, aber sicher: nur eigene Verbindungen
                                                "connect-src 'self'; " +
                                                "object-src 'none'; " +
                                                "base-uri 'self'; " +
                                                "frame-ancestors 'none';"
                                )
                        )
                )
                .requestCache(Customizer.withDefaults());

        // Rate-Limit-Filter vor Auth-Filter einhängen
        http.addFilterBefore(registrationRateLimitFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}