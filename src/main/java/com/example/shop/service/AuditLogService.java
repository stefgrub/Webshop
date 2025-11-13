package com.example.shop.service;

import com.example.shop.domain.AuditLog;
import com.example.shop.repo.AuditLogRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class AuditLogService {

    private final AuditLogRepository repo;
    private static final Set<String> SENSITIVE_KEYS = Set.of(
            "password", "pass", "pwd", "newPassword", "confirmPassword",
            "verificationCode", "code", "token", "csrf", "_csrf"
    );

    public AuditLogService(AuditLogRepository repo) {
        this.repo = repo;
    }

    /** maskt sensible Felder flach in einer Map */
    public Map<String, Object> mask(Map<String, Object> in) {
        if (in == null) return null;
        Map<String, Object> out = new LinkedHashMap<>();
        in.forEach((k, v) -> {
            if (v == null) { out.put(k, null); return; }
            if (SENSITIVE_KEYS.contains(k)) {
                out.put(k, "******");
            } else if (v instanceof String s && s.length() > 2048) {
                out.put(k, s.substring(0, 2048) + "…");
            } else {
                out.put(k, v);
            }
        });
        return out;
    }

    @Transactional
    public AuditLog save(AuditLog log) {
        return repo.save(log);
    }

    @Transactional(readOnly = true)
    public Page<AuditLog> page(int page, int size) {
        return repo.findAll(PageRequest.of(page, size));
    }
}