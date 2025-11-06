package com.example.shop.config;

import com.example.shop.infra.MaintenanceGuard;
import com.example.shop.infra.MaintenanceInterceptor;
import com.example.shop.service.AuditLogService;
import com.example.shop.web.AuditWriteInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final AuditLogService audit;
    private final MaintenanceGuard maintenanceGuard;

    public WebConfig(AuditLogService audit, MaintenanceGuard maintenanceGuard) {
        this.audit = audit;
        this.maintenanceGuard = maintenanceGuard;
    }

    @Bean
    public MaintenanceInterceptor maintenanceInterceptor() {
        return new MaintenanceInterceptor(maintenanceGuard);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(maintenanceInterceptor())
                .order(0)
                .excludePathPatterns(
                        "/maintenance", "/maintenance/**",
                        "/error", "/error/**",
                        "/health", "/actuator/**",
                        "/login", "/logout", "/oauth2/**",
                        "/favicon.ico", "/robots.txt", "/sitemap.xml",
                        "/css/**", "/js/**", "/images/**", "/img/**", "/assets/**", "/webjars/**"
                );

        registry.addInterceptor(new AuditWriteInterceptor(audit))
                .addPathPatterns("/admin/**")
                .order(1);
    }
}