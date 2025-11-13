package com.example.shop.web.admin;

import com.example.shop.settings.SettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/maintenance")
public class AdminMaintenanceController {

    private final SettingsService settings;

    @GetMapping
    public String view(Model model) {
        model.addAttribute("m", settings.readMaintenance()); // nutzt readMaintenance()
        return "admin/maintenance";
    }

    @PostMapping
    public String save(
            @RequestParam(name = "enabled", defaultValue = "false") boolean enabled,
            @RequestParam(name = "message", defaultValue = "") String message,
            @RequestParam(name = "end", required = false) String endIso,
            @RequestParam(name = "homepageOnly", defaultValue = "false") boolean homepageOnly,
            @RequestParam(name = "audience", defaultValue = "ALL") String audience,
            @RequestParam(name = "level", defaultValue = "0") int level,
            @RequestParam(name = "link", required = false) String link
    ) {
        settings.setBool(SettingsService.K_ENABLED, enabled);
        settings.setString(SettingsService.K_MESSAGE, message);
        settings.setString(SettingsService.K_HP_ONLY, Boolean.toString(homepageOnly));
        settings.setString(SettingsService.K_AUDIENCE, audience);
        settings.setInt(SettingsService.K_LEVEL, level);
        settings.setString(SettingsService.K_LINK, link == null ? "" : link.trim());
        settings.setString(SettingsService.K_END, endIso == null ? "" : endIso.trim()); // ISO-8601: 2025-11-11T23:59:00Z

        return "redirect:/admin/maintenance?ok=1";
    }
}