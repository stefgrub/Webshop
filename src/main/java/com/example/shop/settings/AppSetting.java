package com.example.shop.settings;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "app_setting", indexes = {
        @Index(name = "ux_app_setting_key", columnList = "key", unique = true)
})
public class AppSetting {

    @Id
    @Column(name = "key", nullable = false, length = 100)
    private String key;

    // TEXT in Postgres – kein @Lob, sonst will Hibernate OID/CLOB
    @Column(name = "value", columnDefinition = "text")
    private String value;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @PrePersist @PreUpdate
    public void touch() {
        this.updatedAt = OffsetDateTime.now();
    }

    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }

    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }

    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }
}