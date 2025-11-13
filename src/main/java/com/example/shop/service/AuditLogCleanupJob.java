package com.example.shop.service;

import com.example.shop.repo.AuditLogRepo;
import com.example.shop.settings.SettingsService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Component
@RequiredArgsConstructor
public class AuditLogCleanupJob {

    private static final Logger log = LoggerFactory.getLogger(AuditLogCleanupJob.class);

    private final AuditLogRepo repo;
    private final SettingsService settings;

    // Standard-Aufbewahrung 90 Tage, kann via Setting "audit.retention.days" überschrieben werden
    private int retentionDays() {
        return settings.getInt("audit.retention.days", 90);
    }

    // Einmal täglich um 03:17 UTC (ungefähr „mitten in der Nacht“)
    @Scheduled(cron = "0 17 3 * * *", zone = "UTC")
    @Transactional
    public void run() {
        int days = retentionDays();
        if (days <= 0) {
            log.info("Audit-log cleanup skipped (retention disabled: {} days)", days);
            return;
        }
        OffsetDateTime threshold = OffsetDateTime.now(ZoneOffset.UTC).minusDays(days);
        long deleted = repo.deleteByCreatedAtBefore(threshold);
        if (deleted > 0) {
            log.info("Audit-log cleanup: deleted {} entries older than {} days (threshold: {}).", deleted, days, threshold);
        } else {
            log.debug("Audit-log cleanup: nothing to delete (retention {} days).", days);
        }
    }
}