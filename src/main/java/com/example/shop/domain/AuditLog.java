package com.example.shop.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.Map;

@Entity
@Table(
        name = "audit_logs",
        indexes = {
                @Index(name = "idx_audit_logs_created_at", columnList = "created_at"),
                @Index(name = "idx_audit_admin", columnList = "admin_username")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    // alte Felder
    @Column(name = "admin_username")
    private String adminUsername;

    @Column(name = "admin_ip")
    private String adminIp;

    @Column(name = "user_agent", columnDefinition = "text")
    private String userAgent;

    @Column(name = "action", nullable = false)
    private String action;

    @Column(name = "entity_type")
    private String entityType;

    @Column(name = "entity_id")
    private String entityId;

    @Column(name = "path", columnDefinition = "text")
    private String path;

    @Column(name = "request_method")
    private String requestMethod;

    @Column(name = "query_string", columnDefinition = "text")
    private String queryString;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "request_body_masked", columnDefinition = "jsonb")
    private Map<String, Object> requestBodyMasked;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "diff_before_json", columnDefinition = "jsonb")
    private Map<String, Object> diffBeforeJson;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "diff_after_json", columnDefinition = "jsonb")
    private Map<String, Object> diffAfterJson;

    // neue Felder
    @Column(name = "admin", length = 190)
    private String admin;

    @Column(name = "ip", length = 64)
    private String ip;

    @Column(name = "entity", length = 190)
    private String entity;

    @PrePersist
    void prePersist() {
        if (createdAt == null) {
            createdAt = OffsetDateTime.now();
        }
    }
}