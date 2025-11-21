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
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final UserRepo userRepo;
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;
    private final PasswordEncoder passwordEncoder;
    private final SecureRandom random = new SecureRandom();

    @Value("${app.mail.from.address:support@localhost}")
    private String fromAddress;

    @Value("${app.mail.from.name:WebShop}")
    private String fromName;

    // Konfiguration
    private static final Duration CODE_VALIDITY = Duration.ofMinutes(15);
    private static final Duration RESEND_COOLDOWN = Duration.ofMinutes(1);
    private static final int MAX_ATTEMPTS = 10;

    // ----------- Öffentliche Methoden -----------

    @Transactional
    public void sendCode(User user) {
        if (user.isEmailVerified()) {
            throw new IllegalStateException("E-Mail ist bereits verifiziert.");
        }

        Instant now = Instant.now();

        // Spam-Schutz: Cooldown zwischen zwei Sendungen
        if (user.getLastCodeSent() != null &&
                Duration.between(user.getLastCodeSent(), now).compareTo(RESEND_COOLDOWN) < 0) {
            throw new IllegalStateException("Bitte warte einen Moment, bevor du einen neuen Code anforderst.");
        }

        String code = generateCode();
        String hash = passwordEncoder.encode(code);

        user.setVerificationCodeHash(hash);
        user.setVerificationExpires(now.plus(CODE_VALIDITY));
        user.setVerificationAttempts(0); // Versuche zurücksetzen
        user.setLastCodeSent(now);

        userRepo.save(user);

        sendVerificationMail(user, code);
    }

    @Transactional
    public void sendCode(String email) {
        Optional<User> opt = userRepo.findByEmailIgnoreCase(email.trim().toLowerCase());
        if (opt.isEmpty()) {
            throw new IllegalStateException("Unbekannte E-Mail-Adresse.");
        }
        sendCode(opt.get());
    }

    @Transactional
    public void verify(String email, String codePlain) {
        User user = userRepo.findByEmailIgnoreCase(email.trim().toLowerCase())
                .orElseThrow(() -> new IllegalStateException("Unbekannte E-Mail-Adresse."));

        if (user.isEmailVerified()) {
            throw new IllegalStateException("E-Mail ist bereits verifiziert.");
        }

        if (user.getVerificationExpires() == null || user.getVerificationCodeHash() == null) {
            throw new IllegalStateException("Es ist kein gültiger Code hinterlegt. Bitte fordere einen neuen an.");
        }

        Instant now = Instant.now();
        if (now.isAfter(user.getVerificationExpires())) {
            throw new IllegalStateException("Der Code ist abgelaufen. Bitte fordere einen neuen Code an.");
        }

        if (user.getVerificationAttempts() >= MAX_ATTEMPTS) {
            throw new IllegalStateException("Zu viele falsche Eingaben. Bitte fordere einen neuen Code an.");
        }

        user.setVerificationAttempts(user.getVerificationAttempts() + 1);

        if (!passwordEncoder.matches(codePlain, user.getVerificationCodeHash())) {
            userRepo.save(user);
            throw new IllegalStateException("Der Code ist ungültig.");
        }

        // Erfolgreich
        user.setEmailVerified(true);
        user.setVerificationCodeHash(null);
        user.setVerificationExpires(null);
        user.setVerificationAttempts(0);
        user.setLastCodeSent(null);

        userRepo.save(user);

        sendVerifiedMail(user);
    }

    // ----------- private Hilfsmethoden -----------

    private String generateCode() {
        int num = random.nextInt(1_000_000); // 0..999999
        return String.format("%06d", num);
    }

    private void sendVerificationMail(User user, String code) {
        try {
            Context ctx = new Context();
            ctx.setVariable("email", user.getEmail());
            ctx.setVariable("code", code);

            String html = templateEngine.process("mail/verify-email", ctx);

            MimeMessagePreparator prep = mimeMessage -> {
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");
                helper.setTo(user.getEmail());
                helper.setFrom(fromAddress, fromName);
                helper.setSubject("Bitte bestätige deine E-Mail-Adresse");
                helper.setText(html, true);
            };
            mailSender.send(prep);
        } catch (Exception ex) {
            System.err.println("Could not send verification email: " + ex.getMessage());
        }
    }

    private void sendVerifiedMail(User user) {
        try {
            Context ctx = new Context();
            ctx.setVariable("email", user.getEmail());

            String html = templateEngine.process("mail/email-verified", ctx);

            MimeMessagePreparator prep = mimeMessage -> {
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");
                helper.setTo(user.getEmail());
                helper.setFrom(fromAddress, fromName);
                helper.setSubject("E-Mail erfolgreich bestätigt");
                helper.setText(html, true);
            };
            mailSender.send(prep);
        } catch (Exception ex) {
            System.err.println("Could not send 'verified' email: " + ex.getMessage());
        }
    }
}