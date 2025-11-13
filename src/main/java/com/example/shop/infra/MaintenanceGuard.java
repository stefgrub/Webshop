package com.example.shop.infra;

import com.example.shop.settings.SettingsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class MaintenanceGuard implements HandlerInterceptor {

    private final SettingsService settings;

    private static boolean isWhitelisted(String path) {
        if (path == null || path.isEmpty()) return true;
        if (path.equals("/maintenance")) return true;
        if (path.startsWith("/admin")) return true;
        if (path.startsWith("/css/") || path.startsWith("/js/") || path.startsWith("/images/") || path.startsWith("/assets/")) return true;
        if (path.startsWith("/favicon") || path.startsWith("/robots.txt")) return true;
        if (path.startsWith("/actuator")) return true;
        return false;
    }

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) throws Exception {
        var mv = settings.getMaintenanceView();
        if (!mv.enabled()) return true;
        String path = req.getRequestURI();
        if (isWhitelisted(path)) return true;

        // optional: homepageOnly → nur / blockieren (deaktiviert in Guard; Banner steuert das UI)
        res.sendRedirect("/maintenance");
        return false;
    }
}