package com.example.shop.web.controller;

import com.example.shop.settings.SettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class MaintenanceController {

    private final SettingsService settings;

    @GetMapping("/maintenance")
    public String maintenance(Model model) {
        var mv = settings.getMaintenanceView();
        model.addAttribute("maintenance", mv);
        model.addAttribute("message", mv.message()); // wenn Template das braucht
        return "maintenance";
    }
}