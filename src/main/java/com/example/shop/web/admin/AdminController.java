package com.example.shop.web.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("sections", new String[]{"Produkte", "Bestellungen", "Kategorien", "Benutzer"});
        return "admin_dashboard";
    }
}