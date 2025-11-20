package com.example.shop.web;

import com.example.shop.service.MaintenanceService;
import com.example.shop.model.MaintenanceInfo;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalController {

    private final MaintenanceService maintenanceService;

    public GlobalController(MaintenanceService maintenanceService) {
        this.maintenanceService = maintenanceService;
    }

    @ModelAttribute("shopName")
    public String shopName() {
        return "WebShop";
    }
}