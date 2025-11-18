package com.example.shop.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "users")
public class User {

    // --- Getter/Setter ---
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private String role;

    // --- E-Mail-Verifikation ---
    @Column(nullable = false)
    private boolean emailVerified = false;

    private String verificationCodeHash;

    private java.time.Instant verificationExpires;   // <— vollqualifiziert

    @Column(nullable = false)
    private int verificationAttempts = 0;

    private java.time.Instant lastCodeSent;          // <— vollqualifiziert

}