package com.example.shop.web.admin;

import com.example.shop.model.MaintenanceInfo;
import com.example.shop.service.MaintenanceService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.*;

@Controller
@RequestMapping("/admin/maintenance")
@PreAuthorize("hasRole('ADMIN')")
public class AdminMaintenanceController {

    private final MaintenanceService maintenanceService;
    // In Österreich:
    private static final ZoneId ZONE = ZoneId.of("Europe/Vienna");

    public AdminMaintenanceController(MaintenanceService maintenanceService) {
        this.maintenanceService = maintenanceService;
    }

    @GetMapping
    public String page(Model m) {
        MaintenanceInfo mi = maintenanceService.getCurrentMaintenance();

        MaintenanceForm form = new MaintenanceForm();
        form.setMessage(mi != null && mi.getMessage() != null ? mi.getMessage() : "Wartungsarbeiten…");

        // Vorschlags-Ende: jetzt + 2h (lokal)
        LocalDateTime suggested = LocalDateTime.now(ZONE).plusHours(2);
        form.setEndLocal(suggested);

        m.addAttribute("current", mi);
        m.addAttribute("form", form);
        return "admin_maintenance";
    }

    @PostMapping("/activate")
    public String activate(@ModelAttribute("form") MaintenanceForm form) {
        // LocalDateTime (ohne Zone) -> OffsetDateTime (Europe/Vienna)
        LocalDateTime endLocal = form.getEndLocal();
        if (endLocal == null) {
            endLocal = LocalDateTime.now(ZONE).plusHours(2);
        }
        OffsetDateTime end = endLocal.atZone(ZONE).toOffsetDateTime();

        String msg = (form.getMessage() == null || form.getMessage().isBlank())
                ? "Wartungsarbeiten…" : form.getMessage();

        maintenanceService.activate(msg, end);
        return "redirect:/admin/maintenance?activated";
    }

    @PostMapping("/deactivate")
    public String deactivate() {
        maintenanceService.deactivate();
        return "redirect:/admin/maintenance?deactivated";
    }

    // --- Form DTO (innere Klasse oder als separate Datei) ---
    public static class MaintenanceForm {
        @NotBlank
        private String message;

        @NotNull
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        private LocalDateTime endLocal;

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }

        public LocalDateTime getEndLocal() { return endLocal; }
        public void setEndLocal(LocalDateTime endLocal) { this.endLocal = endLocal; }
    }
}