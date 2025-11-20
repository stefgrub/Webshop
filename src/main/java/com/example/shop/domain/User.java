package com.example.shop.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "users")
public class User {

    // Primärschlüssel
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Login / Credentials
    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private String role; // z.B. "USER" oder "ADMIN"

    // --- E-Mail-Verifikation ---
    @Column(nullable = false)
    private boolean emailVerified = false;

    // Hash des 6-stelligen Codes
    private String verificationCodeHash;

    // Ablaufzeitpunkt des Codes
    private java.time.Instant verificationExpires;

    // wie viele Fehlversuche / Eingaben
    @Column(nullable = false)
    private int verificationAttempts = 0;

    // wann zuletzt ein Code gesendet wurde
    private java.time.Instant lastCodeSent;
}