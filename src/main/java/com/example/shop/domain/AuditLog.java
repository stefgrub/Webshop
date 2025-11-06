package com.example.shop.domain;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "audit_logs",
        indexes = {
                @Index(name="idx_audit_created", columnList = "createdAt"),
                @Index(name="idx_audit_admin",   columnList = "adminUsername"),
                @Index(name="idx_audit_entity",  columnList = "entityType,entityId"),
                @Index(name="idx_audit_action",  columnList = "action")
        })
public class AuditLog {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private OffsetDateTime createdAt = OffsetDateTime.now();

    private String adminUsername;
    private String adminIp;
    private String userAgent;

    private String action;
    private String entityType;
    private String entityId;

    @Column(columnDefinition="TEXT") private String path;
    private String requestMethod;
    @Column(columnDefinition="TEXT") private String queryString;
    @Column(columnDefinition="TEXT") private String requestBodyMasked;

    @Column(columnDefinition="TEXT") private String diffBeforeJson;
    @Column(columnDefinition="TEXT") private String diffAfterJson;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public String getAdminUsername() {
        return adminUsername;
    }
    public void setAdminUsername(String adminUsername) {
        this.adminUsername = adminUsername;
    }
    public String getAdminIp() {
        return adminIp;
    }
    public void setAdminIp(String adminIp) {
        this.adminIp = adminIp;
    }
    public String getUserAgent() {
        return userAgent;
    }
    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }
    public String getAction() {
        return action;
    }
    public void setAction(String action) {
        this.action = action;
    }
    public String getEntityType() {
        return entityType;
    }
    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }
    public String getEntityId() {
        return entityId;
    }
    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }
    public String getPath() {
        return path;
    }
    public void setPath(String path) {
        this.path = path;
    }
    public String getRequestMethod() {
        return requestMethod;
    }
    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }
    public String getQueryString() {
        return queryString;
    }
    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }
    public String getRequestBodyMasked() {
        return requestBodyMasked;
    }
    public void setRequestBodyMasked(String requestBodyMasked) {
        this.requestBodyMasked = requestBodyMasked;
    }
    public String getDiffBeforeJson() {
        return diffBeforeJson;
    }
    public void setDiffBeforeJson(String diffBeforeJson) {
        this.diffBeforeJson = diffBeforeJson;
    }
    public String getDiffAfterJson() {
        return diffAfterJson;
    }
    public void setDiffAfterJson(String diffAfterJson) {
        this.diffAfterJson = diffAfterJson;
    }
}