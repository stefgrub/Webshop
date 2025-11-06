package com.example.shop.service;

import com.example.shop.repo.AuditLogRepo;
import org.springframework.transaction.annotation.Transactional; // <— lieber Spring-Transactional
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.OffsetDateTime;

@Component
public class AuditLogCleanupJob {

    private static final Logger log = LoggerFactory.getLogger(AuditLogCleanupJob.class);

    private final AuditLogRepo repo;

    public AuditLogCleanupJob(AuditLogRepo repo) {
        this.repo = repo;
    }

    /**
     * Wird 1x täglich um 02:00 Uhr ausgeführt.
     * Löscht alle AuditLogs, die älter als 30 Tage sind.
     */
    @Transactional
    @Scheduled(cron = "0 0 2 * * *") // täglich um 2:00 Uhr nachts
    public void cleanupOldLogs() {
        OffsetDateTime cutoff = OffsetDateTime.now().minusDays(30);
        int deleted = repo.deleteByCreatedAtBefore(cutoff);
        log.info("🧹 AuditLogCleanupJob: {} alte Logs gelöscht (älter als {}).", deleted, cutoff);
    }
}