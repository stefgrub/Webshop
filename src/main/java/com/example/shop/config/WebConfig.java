package com.example.shop.config;

import com.example.shop.web.AuditWriteInterceptor;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final AuditWriteInterceptor audit;

    @Value("${app.media-dir}")
    private String mediaDir;

    public WebConfig(AuditWriteInterceptor audit) {
        this.audit = audit;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Audit-Interceptor für alle Requests
        registry.addInterceptor(audit).addPathPatterns("/**");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Statische Auslieferung der Medien aus dem Host-Verzeichnis
        registry.addResourceHandler("/media/**")
                .addResourceLocations("file:" + mediaDir + "/");
    }

    /**
     * Registriert einen Filter, der NUR den Request wrappt, damit der Body im Interceptor
     * (via ContentCachingRequestWrapper) auslesbar ist.
     * Die Response wird NICHT gewrapped -> kein Konflikt mit getWriter()/getOutputStream().
     */
    @Bean
    public FilterRegistrationBean<OncePerRequestFilter> contentCachingFilter() {
        OncePerRequestFilter filter = new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain)
                    throws ServletException, IOException {

                ContentCachingRequestWrapper wrappedRequest =
                        new ContentCachingRequestWrapper(request, 1024 * 1024); // bis 1 MB puffern

                // Response unverändert durchreichen
                chain.doFilter(wrappedRequest, response);
            }
        };

        FilterRegistrationBean<OncePerRequestFilter> reg = new FilterRegistrationBean<>(filter);
        reg.setOrder(Ordered.HIGHEST_PRECEDENCE + 20);
        reg.addUrlPatterns("/*");
        return reg;
    }
}