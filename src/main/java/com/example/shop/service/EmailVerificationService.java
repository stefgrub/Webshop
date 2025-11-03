package com.example.shop.service;

import com.example.shop.domain.User;
import com.example.shop.repo.UserRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final UserRepo userRepo;
    private final JavaMailSender mailSender;
    private final PasswordEncoder passwordEncoder;
    private final SpringTemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String fromAddress;

    @Value("${app.mail.from-name:WebShop}")
    private String fromName;

    private static final Duration EXPIRES_IN = Duration.ofMinutes(10);
    private static final Duration RESEND_MIN = Duration.ofSeconds(60);
    private static final int MAX_ATTEMPTS = 3;

    @Transactional
    public void sendCode(String email) {
        User user = userRepo.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Instant now = Instant.now();
        if (user.getLastCodeSent() != null && now.isBefore(user.getLastCodeSent().plus(RESEND_MIN))) {
            throw new IllegalStateException("Bitte warte kurz, bevor du einen neuen Code anforderst.");
        }

        String code = generate6DigitCode();
        user.setVerificationCodeHash(passwordEncoder.encode(code));
        user.setVerificationExpires(now.plus(EXPIRES_IN));
        user.setVerificationAttempts(0);
        user.setLastCodeSent(now);
        userRepo.save(user);

        sendEmail(user.getEmail(), code);
    }

    @Transactional
    public boolean verify(String email, String code) {
        var user = userRepo.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (user.isEmailVerified()) return true;

        if (user.getVerificationExpires() == null || Instant.now().isAfter(user.getVerificationExpires())) {
            throw new IllegalStateException("Der Code ist abgelaufen. Bitte fordere einen neuen an.");
        }
        if (user.getVerificationAttempts() >= MAX_ATTEMPTS) {
            throw new IllegalStateException("Zu viele Versuche. Bitte fordere einen neuen Code an.");
        }

        user.setVerificationAttempts(user.getVerificationAttempts() + 1);

        boolean ok = passwordEncoder.matches(code, user.getVerificationCodeHash());
        if (ok) {
            user.setEmailVerified(true);
            user.setVerificationCodeHash(null);
            user.setVerificationExpires(null);
            user.setVerificationAttempts(0);
            user.setLastCodeSent(null);
            userRepo.save(user);

            sendVerifiedEmail(user.getEmail());
        } else {
            userRepo.save(user);
        }
        return ok;
    }

    private String generate6DigitCode() {
        SecureRandom r = new SecureRandom();
        int n = r.nextInt(1_000_000);
        return String.format("%06d", n);
    }

    private void sendEmail(String to, String code) {
        var ctx = new org.thymeleaf.context.Context(Locale.GERMAN);
        ctx.setVariable("code", code);
        String html = templateEngine.process("mail/verify-code", ctx);

        MimeMessagePreparator prep = mimeMessage -> {
            var helper = new MimeMessageHelper(mimeMessage, "UTF-8");
            helper.setTo(to);
            helper.setFrom(fromAddress, fromName);   // <— Absender + Anzeigename
            helper.setSubject("Dein Bestätigungscode");
            helper.setText(html, true);
        };
        mailSender.send(prep);
    }
    private void sendVerifiedEmail(String to) {
        try {
            var ctx = new org.thymeleaf.context.Context(Locale.GERMAN);
            String html = templateEngine.process("mail/verified", ctx);

            MimeMessagePreparator prep = mimeMessage -> {
                var helper = new MimeMessageHelper(mimeMessage, "UTF-8");
                helper.setTo(to);
                helper.setFrom(fromAddress, fromName); // <— gleiches From
                helper.setSubject("E-Mail erfolgreich bestätigt");
                helper.setText(html, true);
            };
            mailSender.send(prep);
        } catch (Exception ex) {
            // Versandfehler darf den Flow nicht brechen
            System.err.println("Could not send 'verified' email: " + ex.getMessage());
        }
    }
}