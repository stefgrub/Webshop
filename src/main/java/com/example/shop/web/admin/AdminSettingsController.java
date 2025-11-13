package com.example.shop.web.admin;

import com.example.shop.settings.SettingsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/settings")
public class AdminSettingsController {

    private final SettingsService settings;

    public AdminSettingsController(SettingsService settings) {
        this.settings = settings;
    }

    @GetMapping
    public String page(Model model) {
        // Theme
        model.addAttribute("themePrimary", settings.getString("theme.primary", "#4f46e5"));

        // Feature-Flags (einfach als Keys im SettingsService)
        model.addAttribute("checkoutV2", settings.getBool("feature.checkoutV2", false));
        model.addAttribute("liveSearch", settings.getBool("feature.liveSearch", true));

        // E-Mail (Beispiel: Absender)
        model.addAttribute("mailFrom", settings.getString("mail.from", "no-reply@viewwebshop.at"));

        return "admin_settings";
    }

    @PostMapping("/theme")
    public String saveTheme(@RequestParam String primary) {
        settings.setString("theme.primary", primary);
        return "redirect:/admin/settings#appearance";
    }

    @PostMapping("/flag")
    public String saveFlag(@RequestParam String code, @RequestParam(defaultValue="false") boolean enabled) {
        settings.setBool("feature." + code, enabled);
        return "redirect:/admin/settings#features";
    }

    @PostMapping("/mail")
    public String saveMail(@RequestParam String from) {
        settings.setString("mail.from", from);
        return "redirect:/admin/settings#mail";
    }
}