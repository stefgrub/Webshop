package com.example.shop.infra;

import com.example.shop.settings.SettingsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class MaintenanceInterceptor implements HandlerInterceptor {

    private final SettingsService settings;

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) throws Exception {

        // Wartungsmodus aktiv?
        boolean maintenance = settings.getBool("maintenance.enabled", false);
        if (!maintenance) {
            return true;
        }

        String path = req.getRequestURI();

        // Maintenance-Seite selbst erlauben (sonst Endlos-Redirect!)
        if (path.equals("/maintenance") || path.startsWith("/maintenance/")) {
            return true;
        }

        // Admin-Bereich weiterhin zugänglich
        if (path.startsWith("/admin")) {
            return true;
        }

        // Statische Ressourcen und API-Health erlauben
        if (path.startsWith("/css/") ||
                path.startsWith("/js/") ||
                path.startsWith("/img/") ||
                path.startsWith("/images/") ||
                path.startsWith("/media/") ||
                path.startsWith("/assets/") ||
                path.startsWith("/webjars/")) {
            return true;
        }

        if (path.startsWith("/actuator")) {
            return true;
        }

        // Login und Logout sollten auch erreichbar bleiben
        if (path.equals("/login") || path.equals("/logout")) {
            return true;
        }

        // API für Registrierung & Verifikation blockieren (sonst kann man Accounts machen)
        if (path.startsWith("/register") || path.startsWith("/verify")) {
            // Optional: Wenn du möchtest, kann man Registrieren auch im Maintenance erlauben
            // return true;
            res.sendRedirect("/maintenance");
            return false;
        }

        // Alle anderen Seiten auf Maintenance redirecten
        res.sendRedirect("/maintenance");
        return false;
    }
}