package com.example.shop.settings;

import jakarta.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "app_setting")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class AppSetting {
    @Id
    @Column(name = "key", nullable = false, length = 100)
    private String key;

    @Column(name = "value", nullable = false, columnDefinition = "TEXT")
    private String value;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;
}