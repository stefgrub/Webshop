package com.example.shop.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.thymeleaf.extras.springsecurity6.dialect.SpringSecurityDialect;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ITemplateResolver;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;

import java.util.Map;

@Configuration
public class ThymeleafConfig {

    @Value("${shop.name:Mein Webshop}")
    private String shopName;

    @Bean
    @Primary
    public ITemplateResolver templateResolver() {
        SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
        resolver.setPrefix("classpath:/templates/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode(TemplateMode.HTML);
        resolver.setCacheable(false);
        return resolver;
    }

    @Bean
    public SpringTemplateEngine templateEngine(ITemplateResolver resolver) {
        SpringTemplateEngine engine = new SpringTemplateEngine();
        engine.setTemplateResolver(resolver);
        engine.addDialect(new SpringSecurityDialect());
        engine.setEnableSpringELCompiler(true);
        return engine;
    }

    @Bean
    public ThymeleafViewResolver thymeleafViewResolver(SpringTemplateEngine engine) {
        ThymeleafViewResolver resolver = new ThymeleafViewResolver();
        resolver.setTemplateEngine(engine);

        // ✅ Hier werden globale Variablen definiert
        resolver.setStaticVariables(Map.of("shopName", shopName));

        // Optional: UTF-8 sicherstellen
        resolver.setCharacterEncoding("UTF-8");
        return resolver;
    }
}