package com.example.shop.web;

import com.example.shop.settings.SettingsService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.time.ZonedDateTime;

@ControllerAdvice
public class GlobalModelAdvice {

    private final SettingsService settings;

    public GlobalModelAdvice(SettingsService settings) {
        this.settings = settings;
    }

    @ModelAttribute("maintenanceActive")
    public boolean maintenanceActive() {
        return settings.getBool("maintenance.enabled", false);
    }

    @ModelAttribute("now")
    public ZonedDateTime now() { return ZonedDateTime.now(); }
}