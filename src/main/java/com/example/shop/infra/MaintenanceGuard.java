package com.example.shop.infra;

import com.example.shop.settings.SettingsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Guard für den "Maintenance View" – nutzt getMaintenanceView().
 * Dieser Guard ist dafür gedacht, den allgemeinen Zugriff auf die Seite
 * während Wartungsarbeiten zu beschränken und auf /maintenance umzuleiten.
 *
 * Wichtig:
 *  - /maintenance selbst MUSS immer erlaubt sein, sonst Endlos-Redirect.
 *  - Admin-Bereich, statische Assets, Actuator etc. bleiben freigegeben.
 */
@Component
@RequiredArgsConstructor
public class MaintenanceGuard implements HandlerInterceptor {

    private final SettingsService settings;

    private static boolean isWhitelisted(String path) {
        if (path == null || path.isEmpty()) {
            return true;
        }

        // Maintenance-Seite selbst immer erlauben
        if (path.equals("/maintenance") || path.startsWith("/maintenance/")) {
            return true;
        }

        // Admin-Bereich weiter zulassen
        if (path.startsWith("/admin")) {
            return true;
        }

        // Statische Ressourcen
        if (path.startsWith("/css/") ||
                path.startsWith("/js/") ||
                path.startsWith("/img/") ||
                path.startsWith("/images/") ||
                path.startsWith("/media/") ||
                path.startsWith("/assets/") ||
                path.startsWith("/webjars/")) {
            return true;
        }

        // Favicon & robots.txt
        if (path.startsWith("/favicon") || path.equals("/robots.txt")) {
            return true;
        }

        // Health / Actuator
        if (path.startsWith("/actuator")) {
            return true;
        }

        // Login / Logout weiter erlauben (optional)
        if (path.equals("/login") || path.equals("/logout")) {
            return true;
        }

        return false;
    }

    @Override
    public boolean preHandle(HttpServletRequest req,
                             HttpServletResponse res,
                             Object handler) throws Exception {

        var mv = settings.getMaintenanceView();
        if (mv == null || !mv.enabled()) {
            // Wartungsmodus (über MaintenanceView) ist nicht aktiv
            return true;
        }

        String path = req.getRequestURI();
        if (isWhitelisted(path)) {
            return true;
        }

        // Optional: homepageOnly berücksichtigen
        // Falls du das Flag nutzen willst:
        // if (mv.homepageOnly() && !"/".equals(path)) {
        //     // nur Startseite blockieren, alles andere durchlassen
        //     return true;
        // }

        // Ab hier wird der Zugriff blockiert → Redirect auf Maintenance-Seite
        res.sendRedirect(req.getContextPath() + "/maintenance");
        return false;
    }
}