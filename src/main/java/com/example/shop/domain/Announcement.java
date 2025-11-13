package com.example.shop.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "announcement")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Announcement {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "active", nullable = false)
    private boolean active;

    @Column(name = "audience", nullable = false, length = 16)
    private String audience;

    @Column(name = "from_ts")
    private OffsetDateTime fromTs;

    @Column(name = "to_ts")
    private OffsetDateTime toTs;

    @Column(name = "level", nullable = false)
    private int level;

    @Column(name = "link_url", length = 512)
    private String linkUrl;

    @Column(name = "title", nullable = false, length = 160)
    private String title;

    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @Column(name = "updated_by", length = 120, nullable = false)
    private String updatedBy;

    @Transient
    public String levelCss() {
        // passt zum Template: class="alert" + levelCss()
        // 2: error, 1: warn, sonst info
        return switch (level) {
            case 2 -> "alert--danger";
            case 1 -> "alert--warn";
            default -> "alert--info";
        };
    }
}