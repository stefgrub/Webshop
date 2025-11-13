package com.example.shop.config;

import com.example.shop.web.AuditWriteInterceptor;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final AuditWriteInterceptor audit;

    public WebConfig(AuditWriteInterceptor audit) {
        this.audit = audit;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Wir wollen den Body für alle Requests verfügbar machen – filtern in der Interceptor-Logik
        registry.addInterceptor(audit).addPathPatterns("/**");
    }

    /**
     * Registriert einen Filter, der Request/Response wrappt, damit der Body im Interceptor
     * (via ContentCachingRequestWrapper) auslesbar ist.
     */
    @Bean
    public FilterRegistrationBean<OncePerRequestFilter> contentCachingFilter() {
        OncePerRequestFilter filter = new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain) throws ServletException, IOException {

                ContentCachingRequestWrapper wrappedRequest =
                        new ContentCachingRequestWrapper(request, 1024 * 1024); // bis 1 MB puffern
                ContentCachingResponseWrapper wrappedResponse =
                        new ContentCachingResponseWrapper(response);

                try {
                    chain.doFilter(wrappedRequest, wrappedResponse);
                } finally {
                    // Response-Body zurück in den echten Response kopieren
                    wrappedResponse.copyBodyToResponse();
                }
            }
        };

        FilterRegistrationBean<OncePerRequestFilter> reg = new FilterRegistrationBean<>(filter);
        reg.setOrder(Ordered.HIGHEST_PRECEDENCE + 20);
        reg.addUrlPatterns("/*");
        return reg;
    }
}