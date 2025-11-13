package com.example.shop.repo;

import com.example.shop.domain.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long>, PagingAndSortingRepository<AuditLog, Long> {
}