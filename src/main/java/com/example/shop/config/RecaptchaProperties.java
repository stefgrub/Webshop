package com.example.shop.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "recaptcha")
public class RecaptchaProperties {

    /**
     * Globaler Schalter: reCAPTCHA an/aus.
     */
    private boolean enabled;

    /**
     * Site-Key (für HTML-Formular).
     */
    private String siteKey;

    /**
     * Secret-Key (für Server-Validierung).
     */
    private String secretKey;

}