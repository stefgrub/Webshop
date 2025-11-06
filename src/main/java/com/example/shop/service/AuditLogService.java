package com.example.shop.service;

import com.example.shop.domain.AuditLog;
import com.example.shop.repo.AuditLogRepo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

@Service
public class AuditLogService {
    private final AuditLogRepo repo;
    private final ObjectMapper om = new ObjectMapper();
    private static final Set<String> SENSITIVE = Set.of("password","pass","pwd","token","secret");

    public AuditLogService(AuditLogRepo repo) { this.repo = repo; }

    @Transactional public void save(AuditLog log) { repo.save(log); }

    public String mask(Map<String, ?> form) {
        if (form == null) return null;
        var copy = new LinkedHashMap<String,Object>();
        form.forEach((k,v) -> copy.put(k, SENSITIVE.contains(k.toLowerCase()) ? "***" : v));
        try { return om.writerWithDefaultPrettyPrinter().writeValueAsString(copy); }
        catch (Exception e) { return null; }
    }

    public String toJson(Object obj) {
        if (obj == null) return null;
        try { return om.writerWithDefaultPrettyPrinter().writeValueAsString(obj); }
        catch (Exception e) { return null; }
    }
}