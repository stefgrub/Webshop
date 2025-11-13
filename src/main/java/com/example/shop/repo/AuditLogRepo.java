package com.example.shop.repo;

import com.example.shop.domain.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;

public interface AuditLogRepo extends JpaRepository<AuditLog, Long> {

    // Für die Admin-Ansicht: neueste Einträge zuerst, mit Paging
    Page<AuditLog> findAllByOrderByCreatedAtDesc(Pageable pageable);

    // Für den Cleanup-Job: Alte Einträge löschen
    long deleteByCreatedAtBefore(OffsetDateTime threshold);
}