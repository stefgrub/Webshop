package com.example.shop.service;

import com.example.shop.config.RecaptchaProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Service
public class RecaptchaService {

    private static final Logger log = LoggerFactory.getLogger(RecaptchaService.class);

    private final RecaptchaProperties properties;
    private final RestClient restClient;

    public RecaptchaService(RecaptchaProperties properties) {
        this.properties = properties;
        this.restClient = RestClient.builder()
                .baseUrl("https://www.google.com/recaptcha/api/siteverify")
                .build();
    }

    /**
     * Prüft den reCAPTCHA-Token. Wirf KEINE Exception, sondern gib false zurück,
     * wenn etwas schiefgeht – so produzieren wir keinen 500er.
     */
    public boolean isCaptchaValid(String responseToken, String userIp) {
        // Wenn deaktiviert: nicht validieren (für dev/test)
        if (!properties.isEnabled()) {
            return true;
        }

        if (responseToken == null || responseToken.isBlank()) {
            log.warn("reCAPTCHA: Kein response token übermittelt");
            return false;
        }

        try {
            Map<String, Object> result = restClient.post()
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body("secret=" + properties.getSecretKey()
                            + "&response=" + responseToken
                            + (userIp != null ? "&remoteip=" + userIp : "")
                    )
                    .retrieve()
                    .body(Map.class);

            if (result == null) {
                log.warn("reCAPTCHA: Antwort war null");
                return false;
            }

            Object success = result.get("success");
            boolean ok = (success instanceof Boolean b && b);

            if (!ok) {
                log.warn("reCAPTCHA: Validierung fehlgeschlagen: {}", result);
            }

            return ok;
        } catch (Exception ex) {
            // WICHTIG: Keine Exception nach außen → kein 500er
            log.error("Fehler bei reCAPTCHA-Validierung", ex);
            return false;
        }
    }
}