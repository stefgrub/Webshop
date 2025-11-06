package com.example.shop.web.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/users/entry")
public class AdminUserEntryController {

    @GetMapping
    public String showPage(Model model) {
        model.addAttribute("title", "Admin - Benutzer-Eingabe");
        return "admin_users_entry";
    }
}