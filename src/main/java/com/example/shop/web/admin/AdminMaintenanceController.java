package com.example.shop.web.admin;

import com.example.shop.settings.SettingsService;
import jakarta.validation.constraints.NotBlank;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Controller
@RequestMapping({"/admin/maintenance", "/admin/settings/maintenance"})
public class AdminMaintenanceController {

    private static final ZoneId ZONE = ZoneId.of("Europe/Vienna");
    private final SettingsService settings;

    public AdminMaintenanceController(SettingsService settings) {
        this.settings = settings;
    }

    @GetMapping
    public String form(Model model) {
        var view = settings.readMaintenance(); // liefert u.a. view.end() als ZonedDateTime oder null

        var f = new Form();
        boolean enabledFlag = settings.getBool(SettingsService.K_ENABLED, false);
        f.setEnabled(enabledFlag);
        f.setMessage(view.message());
        f.setHomepageOnly(view.homepageOnly());

        // ZonedDateTime (aus Settings) -> LocalDateTime fürs <input type="datetime-local">
        if (view.end() != null) {
            f.setEnd(view.end().withZoneSameInstant(ZONE).toLocalDateTime());
        } else {
            f.setEnd(null);
        }

        model.addAttribute("form", f);
        return "admin_maintenance";
    }

    @PostMapping
    public String save(@Validated @ModelAttribute("form") Form form,
                       RedirectAttributes ra) {

        // LocalDateTime (aus Formular) -> ZonedDateTime (Europe/Vienna) für die Settings
        ZonedDateTime endZdt = null;
        LocalDateTime endLdt = form.getEnd();
        if (endLdt != null) {
            endZdt = endLdt.atZone(ZONE);
        }

        settings.setBool(SettingsService.K_ENABLED, form.isEnabled());
        settings.setString(SettingsService.K_MESSAGE, form.getMessage() == null ? "" : form.getMessage().trim());
        settings.setZonedDateTime(SettingsService.K_END, endZdt);
        settings.setBool(SettingsService.K_HP_ONLY, form.isHomepageOnly());

        ra.addFlashAttribute("saved", true);
        return "redirect:/admin/maintenance";
    }

    @Validated
    public static class Form {
        private boolean enabled;

        @NotBlank(message = "Bitte eine Banner-Nachricht angeben.")
        private String message;

        private boolean homepageOnly;

        // WICHTIG: LocalDateTime für <input type="datetime-local">
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        private LocalDateTime end;

        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }

        public boolean isHomepageOnly() { return homepageOnly; }
        public void setHomepageOnly(boolean homepageOnly) { this.homepageOnly = homepageOnly; }

        public LocalDateTime getEnd() { return end; }
        public void setEnd(LocalDateTime end) { this.end = end; }
    }
}