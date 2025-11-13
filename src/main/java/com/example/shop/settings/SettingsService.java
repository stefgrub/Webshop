package com.example.shop.settings;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SettingsService {

    // ---- Keys (von AdminMaintenanceController erwartet) ----
    public static final String K_ENABLED  = "maintenance.enabled";
    public static final String K_MESSAGE  = "maintenance.message";
    public static final String K_END      = "maintenance.end";           // ISO-8601, z.B. 2025-11-11T23:59:00Z
    public static final String K_HP_ONLY  = "maintenance.homepageOnly";
    public static final String K_AUDIENCE = "maintenance.audience";
    public static final String K_LEVEL    = "maintenance.level";
    public static final String K_LINK     = "maintenance.link";

    private final AppSettingRepository repo;

    // ------------ basic get/set ------------
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
        s.setUpdatedAt(OffsetDateTime.now(ZoneOffset.UTC));
        repo.save(s);
    }

    // ------------ typed helpers ------------
    @Transactional(readOnly = true)
    public String getString(String key, String def) {
        return get(key).orElse(def);
    }

    @Transactional
    public void setString(String key, String value) {  // <— von AdminSettingsController erwartet
        set(key, value == null ? "" : value);
    }

    @Transactional(readOnly = true)
    public boolean getBool(String key, boolean def) {
        return get(key).map(v -> {
            String t = v.trim().toLowerCase();
            return t.equals("true") || t.equals("1") || t.equals("yes") || t.equals("on");
        }).orElse(def);
    }

    @Transactional public void setBool(String key, boolean value) { set(key, Boolean.toString(value)); }

    @Transactional(readOnly = true)
    public int getInt(String key, int def) {
        return get(key).map(v -> {
            try { return Integer.parseInt(v.trim()); } catch (Exception e) { return def; }
        }).orElse(def);
    }

    @Transactional public void setInt(String key, int value) { set(key, Integer.toString(value)); }

    @Transactional(readOnly = true)
    public OffsetDateTime getDateTime(String key) {
        return get(key).map(String::trim).filter(s -> !s.isEmpty()).map(s -> {
            try { return OffsetDateTime.parse(s); } catch (DateTimeParseException e) { return null; }
        }).orElse(null);
    }

    // ------------ Maintenance ViewModel ------------
    public static record MaintenanceView(
            boolean enabled,
            String audience,
            int level,
            String message,
            String linkUrl,
            boolean homepageOnly,
            OffsetDateTime end
    ) {}

    /** Wird vom Guard/Advice & Admin-UI genutzt */
    @Transactional(readOnly = true)
    public MaintenanceView getMaintenanceView() {
        boolean enabled = getBool(K_ENABLED, false);
        String audience = getString(K_AUDIENCE, "ALL");
        int level = getInt(K_LEVEL, 0);
        String message = getString(K_MESSAGE, "");
        String link = get(K_LINK).map(String::trim).filter(s -> !s.isEmpty()).orElse(null);
        boolean homepageOnly = getBool(K_HP_ONLY, false);
        OffsetDateTime end = getDateTime(K_END);
        return new MaintenanceView(enabled, audience, level, message, link, homepageOnly, end);
    }

    /** Von AdminMaintenanceController erwartet */
    @Transactional(readOnly = true)
    public MaintenanceView readMaintenance() {
        return getMaintenanceView();
    }
}