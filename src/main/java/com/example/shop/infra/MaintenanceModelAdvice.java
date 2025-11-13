package com.example.shop.infra;

import com.example.shop.settings.SettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
@RequiredArgsConstructor
public class MaintenanceModelAdvice {

    private final SettingsService settings;

    @ModelAttribute("maintenance")
    public SettingsService.MaintenanceView maintenance() {
        return settings.getMaintenanceView();
    }

    @ModelAttribute("shopName")
    public String shopName() {
        return settings.getString("shop.name", "WebShop");
    }
}