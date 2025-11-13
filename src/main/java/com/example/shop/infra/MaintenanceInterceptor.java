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
        boolean maintenance = settings.getBool("maintenance.enabled", false);
        if (!maintenance) return true;

        String path = req.getRequestURI();

        // Admin-Bereich erlauben
        if (path.startsWith("/admin")) return true;

        // Statische Dateien/API-Health/Actuator erlauben
        if (path.startsWith("/css/") || path.startsWith("/js/") || path.startsWith("/images/")) return true;
        if (path.startsWith("/actuator")) return true;

        // Alles andere auf Maintenance-Seite leiten
        res.sendRedirect("/maintenance");
        return false;
    }
}