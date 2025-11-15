package com.example.shop.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class CacheConfig {

    @Bean
    public Caffeine<Object, Object> caffeineConfig() {
        // Konfiguration: max. 10.000 Einträge, nach 10 Minuten Inaktivität raus
        return Caffeine.newBuilder()
                .maximumSize(10_000)
                .expireAfterAccess(10, TimeUnit.MINUTES);
    }

    @Bean
    public CacheManager cacheManager(Caffeine<Object, Object> caffeine) {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager(
                "products",      // Liste der Produkte
                "product",       // Einzelnes Produkt-Detail
                "categories",    // Kategorien (falls du welche hast)
                "homepage"       // Daten fürs Home-Template
        );
        cacheManager.setCaffeine(caffeine);
        return cacheManager;
    }
}