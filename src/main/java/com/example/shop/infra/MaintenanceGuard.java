package com.example.shop.infra;

import com.example.shop.settings.SettingsService;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Statement;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Kombiniert DB-Gesundheit und vorhandene Wartungs-Settings.
 * Cachet Ergebnisse kurz, um nicht bei jedem Request einen DB-Ping zu machen.
 */
@Service
public class MaintenanceGuard {

    private final DataSource dataSource;
    private final SettingsService settings;

    private volatile long lastCheckNanos = 0L;
    private final AtomicBoolean lastDbUp = new AtomicBoolean(true);
    private static final Duration TTL = Duration.ofSeconds(20); // alle 20s neu prüfen

    public MaintenanceGuard(DataSource dataSource, SettingsService settings) {
        this.dataSource = dataSource;
        this.settings = settings;
    }

    public boolean isDatabaseUp() {
        long now = System.nanoTime();
        if (now - lastCheckNanos < TTL.toNanos()) {
            return lastDbUp.get();
        }
        boolean up;
        try (var conn = dataSource.getConnection(); Statement st = conn.createStatement()) {
            st.setQueryTimeout(1);      // 1 Sekunde
            st.execute("SELECT 1");     // Lightweight Health-Check
            up = true;
        } catch (Exception e) {
            up = false;
        }
        lastDbUp.set(up);
        lastCheckNanos = now;
        return up;
    }

    /** Settings aus DB (Banner/Wartung). Fällt DB komplett aus, kommen wir hier evtl. nicht hin – deshalb fallback. */
    public SettingsService.MaintenanceView readSettingsSafe() {
        try {
            return settings.readMaintenance();
        } catch (Exception e) {
            // Wenn Settings nicht gelesen werden können (z.B. DB down), liefern wir "inaktiv" zurück
            return new SettingsService.MaintenanceView(false, "", null, false);
        }
    }

    /** True, wenn DB down ODER Settings "aktiv". */
    public boolean maintenanceActiveGlobal() {
        boolean dbUp = isDatabaseUp();
        var s = readSettingsSafe();
        boolean settingsActive = s.active();
        return !dbUp || settingsActive;
    }

    /** Nur für Startseite: Settings mit homepageOnly beachten. DB-Down überschreibt und ist immer global. */
    public boolean maintenanceActiveForPath(String path) {
        if (!isDatabaseUp()) return true; // DB down -> immer aktiv (global)
        var s = readSettingsSafe();
        if (!s.active()) return false;
        if (Boolean.TRUE.equals(s.homepageOnly())) {
            return "/".equals(path) || "/index" .equals(path) || "/index.html".equals(path);
        }
        return true; // Settings aktiv & nicht auf Homepage beschränkt
    }

    public String message() {
        var s = readSettingsSafe();
        if (!isDatabaseUp()) {
            // Präzise Meldung bei Störung – fällt auf die bestehende Nachricht zurück, wenn vorhanden
            return (s.message() != null && !s.message().isBlank())
                    ? s.message()
                    : "Wartungsarbeiten: Datenbank derzeit nicht erreichbar.";
        }
        return (s.message() == null || s.message().isBlank())
                ? "Wartungsarbeiten: Es kann zu Unterbrechungen kommen."
                : s.message();
    }
}