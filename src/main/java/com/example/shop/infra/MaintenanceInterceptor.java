package com.example.shop.infra;

import org.springframework.http.HttpStatus;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

public class MaintenanceInterceptor implements HandlerInterceptor {

    public static final String ATTR_FWD = "MAINTENANCE_FORWARD_DONE";

    private final MaintenanceGuard guard;
    private final AntPathMatcher matcher = new AntPathMatcher();

    // Immer erlaubte Pfade (Seite, Assets, Fehler, Health)
    private final List<String> whitelist = List.of(
            "/maintenance", "/maintenance/**",
            "/error", "/error/**",
            "/health", "/actuator/**",
            "/login", "/logout", "/oauth2/**",
            "/favicon.ico", "/robots.txt", "/sitemap.xml",
            "/css/**", "/js/**", "/images/**", "/img/**", "/assets/**", "/fonts/**", "/webjars/**"
    );

    // Bereiche, die wir im "weichen" Wartungsmodus (Settings aktiv) für Nicht-Admins sperren
    private final List<String> restrictedWhenSettingsActive = List.of(
            "/checkout/**", "/orders/**", "/cart/**", "/account/**", "/profile/**"
    );

    public MaintenanceInterceptor(MaintenanceGuard guard) {
        this.guard = guard;
    }

    private boolean matchesAny(String path, List<String> patterns) {
        if (path == null || path.isBlank()) return false;
        String p = (path.endsWith("/") && path.length() > 1) ? path.substring(0, path.length() - 1) : path;
        return patterns.stream().anyMatch(w -> matcher.match(w, p));
    }

    private boolean isWhitelisted(String path) {
        return matchesAny(path, whitelist);
    }

    private boolean isRestrictedForSoftMode(String path) {
        return matchesAny(path, restrictedWhenSettingsActive);
    }

    private boolean isHome(String path) {
        return "/".equals(path) || "/index".equals(path) || "/index.html".equals(path);
    }

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) throws Exception {
        final String path = req.getRequestURI();
        if (isWhitelisted(path)) return true;
        if (req.getAttribute(ATTR_FWD) != null) return true; // Loop-Schutz

        // 1) Harte Regel: DB down => global 503 (Admins auch), außer Whitelist
        if (!guard.isDatabaseUp()) {
            req.setAttribute(ATTR_FWD, Boolean.TRUE);
            res.setStatus(HttpStatus.SERVICE_UNAVAILABLE.value());
            req.setAttribute("maintenanceMessage", guard.message());
            req.getRequestDispatcher("/maintenance").forward(req, res);
            return false;
        }

        // 2) Weicher Modus: Settings aktiv? Dann nur selektiv für Nicht-Admins sperren
        var settings = guard.readSettingsSafe();
        if (settings.active()) {

            // Admins kommen durch, damit du weiterarbeiten kannst
            boolean isAdmin = req.isUserInRole("ADMIN");

            if (!isAdmin) {
                boolean enforce;
                if (Boolean.TRUE.equals(settings.homepageOnly())) {
                    // nur Startseite blocken
                    enforce = isHome(path);
                } else {
                    // kritische Bereiche blocken (Checkout/Cart/Orders/Account)
                    enforce = isRestrictedForSoftMode(path);
                }

                if (enforce) {
                    req.setAttribute(ATTR_FWD, Boolean.TRUE);
                    res.setStatus(HttpStatus.SERVICE_UNAVAILABLE.value());
                    req.setAttribute("maintenanceMessage", guard.message());
                    req.getRequestDispatcher("/maintenance").forward(req, res);
                    return false;
                }
            }
        }

        // Alles andere erlauben (Katalog, Produkte, Suche …)
        return true;
    }
}