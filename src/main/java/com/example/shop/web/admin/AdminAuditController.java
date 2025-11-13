package com.example.shop.web.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/audit")
public class AdminAuditController {

    @GetMapping
    public String list(Model model) {
        // Optional: später Audit-Daten befüllen
        return "admin/audit/index";
    }
}