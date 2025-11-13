package com.example.shop.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "feature_flag")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class FeatureFlag {
    @Id
    @Column(name = "code", nullable = false, length = 80)
    private String code;

    @Column(name = "enabled", nullable = false)
    private boolean enabled;
}