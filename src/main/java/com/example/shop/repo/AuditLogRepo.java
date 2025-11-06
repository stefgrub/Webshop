package com.example.shop.repo;

import com.example.shop.domain.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.OffsetDateTime;
import org.springframework.transaction.annotation.Transactional;

public interface AuditLogRepo extends JpaRepository<AuditLog, Long> {
    Page<AuditLog> findByEntityTypeContainingIgnoreCaseOrActionContainingIgnoreCaseOrAdminUsernameContainingIgnoreCase(
            String et, String ac, String user, Pageable pageable);

    @Transactional
    int deleteByCreatedAtBefore(OffsetDateTime cutoff);
}