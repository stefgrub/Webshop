package com.example.shop.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

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

    // --- Getter/Setter ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public boolean isEmailVerified() { return emailVerified; }
    public void setEmailVerified(boolean emailVerified) { this.emailVerified = emailVerified; }

    public String getVerificationCodeHash() { return verificationCodeHash; }
    public void setVerificationCodeHash(String verificationCodeHash) { this.verificationCodeHash = verificationCodeHash; }

    public java.time.Instant getVerificationExpires() { return verificationExpires; }
    public void setVerificationExpires(java.time.Instant verificationExpires) { this.verificationExpires = verificationExpires; }

    public int getVerificationAttempts() { return verificationAttempts; }
    public void setVerificationAttempts(int verificationAttempts) { this.verificationAttempts = verificationAttempts; }

    public java.time.Instant getLastCodeSent() { return lastCodeSent; }
    public void setLastCodeSent(java.time.Instant lastCodeSent) { this.lastCodeSent = lastCodeSent; }
}