package com.example.shop.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "mail_template")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class MailTemplate {
    @Id
    @Column(name = "code", nullable = false, length = 80)
    private String code;

    @Column(name = "subject", nullable = false)
    private String subject;

    @Column(name = "body_html", nullable = false, columnDefinition = "TEXT")
    private String bodyHtml;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}