package com.example.shop.settings;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.*;
import java.util.Optional;

@Service
public class SettingsService {
    private final AppSettingRepository repo;
    private static final ZoneId ZONE = ZoneId.of("Europe/Vienna");

    public SettingsService(AppSettingRepository repo) { this.repo = repo; }

    @Transactional(readOnly = true)
    public Optional<String> get(String key) {
        return repo.findByKey(key).map(AppSetting::getValue);
    }

    @Transactional
    public void set(String key, String value) {
        AppSetting s = repo.findByKey(key).orElseGet(() -> {
            AppSetting x = new AppSetting();
            x.setKey(key);
            return x;
        });
        s.setValue(value);
        repo.save(s);
    }

    // typed helpers
    public boolean getBool(String key, boolean def) {
        return get(key).map(v -> "true".equalsIgnoreCase(v.trim())).orElse(def);
    }

    public void setBool(String key, boolean value) { set(key, Boolean.toString(value)); }

    public String getString(String key, String def) {
        return get(key).map(String::trim).filter(s -> !s.isEmpty()).orElse(def);
    }

    public void setString(String key, String value) { set(key, value == null ? "" : value); }

    public ZonedDateTime getZonedDateTime(String key) {
        String raw = get(key).map(String::trim).orElse(null);
        if (raw == null || raw.isEmpty() || "null".equalsIgnoreCase(raw)) {
            return null;
        }
        try {
            return ZonedDateTime.parse(raw);
        } catch (Exception ignore) { }
        try {
            return OffsetDateTime.parse(raw).toZonedDateTime();
        } catch (Exception ignore) { }
        try {
            return LocalDateTime.parse(raw).atZone(ZONE);
        } catch (Exception ignore) { }
        try {
            return LocalDate.parse(raw).atStartOfDay(ZONE);
        } catch (Exception ignore) { }
        return null;
    }

    public void setZonedDateTime(String key, ZonedDateTime zdt) {
        set(key, zdt == null ? "" : zdt.toString());
    }

    // Convenience-Keys
    public static final String K_ENABLED = "maintenance.enabled";
    public static final String K_MESSAGE = "maintenance.message";
    public static final String K_END = "maintenance.end";
    public static final String K_HP_ONLY = "maintenance.homepageOnly";

    // View-Daten fürs Template
    public MaintenanceView readMaintenance() {
        boolean enabled = getBool(K_ENABLED, false);
        String message = getString(K_MESSAGE, "");
        ZonedDateTime end = getZonedDateTime(K_END);
        boolean homepageOnly = getBool(K_HP_ONLY, false);

        // "aktiv" nur wenn enabled und end nicht abgelaufen
        boolean active = enabled && (end == null || end.isAfter(ZonedDateTime.now(ZONE)));
        return new MaintenanceView(active, (message == null ? "" : message), end, homepageOnly);
    }

    public record MaintenanceView(boolean active, String message, ZonedDateTime end, boolean homepageOnly) {}
}