package com.example.shop.web.controller;

import com.example.shop.infra.MaintenanceGuard;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpServletResponse;  // <-- jakarta

@Controller
public class MaintenanceController {

    private final MaintenanceGuard guard;

    public MaintenanceController(MaintenanceGuard guard) {
        this.guard = guard;
    }

    @GetMapping("/maintenance")
    public String maintenance(Model model, HttpServletResponse res) {
        res.setStatus(HttpStatus.SERVICE_UNAVAILABLE.value());
        model.addAttribute("message", guard.message());
        return "maintenance";
    }
}