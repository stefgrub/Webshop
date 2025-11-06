package com.example.shop.web;

import com.example.shop.settings.SettingsService;
import com.example.shop.settings.SettingsService.MaintenanceView;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.ui.Model;

@ControllerAdvice(annotations = Controller.class)
public class GlobalModelAdvice {

    private final SettingsService settings;

    public GlobalModelAdvice(SettingsService settings) {
        this.settings = settings;
    }

    @ModelAttribute
    public void addGlobals(Model model) {
        try {
            MaintenanceView m = settings.readMaintenance();
            model.addAttribute("maintenance", m);
        } catch (Exception ex) {
            // Fail-safe: niemals das gesamte Rendering crashen
            model.addAttribute("maintenance", new MaintenanceView(false, "", null, false));
        }
    }
}