package com.example.shop.web.auth;

import com.example.shop.domain.User;
import com.example.shop.repo.UserRepo;
import com.example.shop.service.EmailVerificationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriUtils;
import java.nio.charset.StandardCharsets;

@Controller
public class RegistrationController {

    private final UserRepo users;
    private final PasswordEncoder encoder;
    private final EmailVerificationService emailVerificationService;

    public RegistrationController(UserRepo users, PasswordEncoder encoder,
                                  EmailVerificationService emailVerificationService) {
        this.users = users;
        this.encoder = encoder;
        this.emailVerificationService = emailVerificationService;
    }

    // --- DTO-Klasse mit Bean Validation ---
    public static class RegisterForm {

        @NotBlank(message = "E-Mail darf nicht leer sein.")
        @Email(message = "Bitte eine gültige E-Mail-Adresse eingeben.")
        private String email;

        @NotBlank(message = "Passwort darf nicht leer sein.")
        @Size(min = 8, message = "Passwort muss mindestens 8 Zeichen haben.")
        private String password;

        @NotBlank(message = "Bitte Passwort-Wiederholung eingeben.")
        private String passwordRepeat;

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getPasswordRepeat() { return passwordRepeat; }
        public void setPasswordRepeat(String passwordRepeat) { this.passwordRepeat = passwordRepeat; }
    }

    // --- GET: Formular anzeigen ---
    @GetMapping("/register")
    public String form(Model m) {
        m.addAttribute("form", new RegisterForm());
        return "register";
    }

    // --- POST: Registrierung absenden ---
    @PostMapping("/register")
    public String submit(@Valid @ModelAttribute("form") RegisterForm f,
                         BindingResult result,
                         Model m) {

        if (result.hasErrors()) {
            return "register";
        }

        if (!f.getPassword().equals(f.getPasswordRepeat())) {
            result.rejectValue("passwordRepeat", "error.passwordRepeat", "Passwörter stimmen nicht überein.");
            return "register";
        }

        var existing = users.findByEmailIgnoreCase(f.getEmail());
        if (existing.isPresent()) {
            User u = existing.get();
            if (u.isEmailVerified()) {
                result.rejectValue("email", "error.email", "E-Mail ist bereits registriert.");
                return "register";
            } else {
                emailVerificationService.sendCode(u.getEmail());
                return "redirect:/verify?email=" + UriUtils.encode(u.getEmail(), StandardCharsets.UTF_8);
            }
        }
        if (existing.isPresent() && !existing.get().isEmailVerified()) {
            emailVerificationService.sendCode(existing.get().getEmail());
            return "redirect:/verify?email=" + UriUtils.encode(existing.get().getEmail(), StandardCharsets.UTF_8);
        }

        User u = new User();
        u.setEmail(f.getEmail());
        u.setPasswordHash(encoder.encode(f.getPassword()));
        u.setRole("USER");
        users.save(u);

        // Code senden & Weiterleitung
        emailVerificationService.sendCode(u.getEmail());
        return "redirect:/verify?email=" + u.getEmail();
    }

    // --- Verify-Flow ---
    @GetMapping("/verify")
    public String verifyForm(@RequestParam("email") String email, Model model) {
        users.findByEmailIgnoreCase(email).orElseThrow();
        model.addAttribute("email", email);
        return "auth/verify";
    }

    @PostMapping("/verify")
    public String verifySubmit(@RequestParam("email") String email,
                               @RequestParam("code") String code,
                               Model model) {
        try {
            boolean ok = emailVerificationService.verify(email, code);
            model.addAttribute("email", email);

            if (ok) {
                model.addAttribute("success", "Deine E-Mail wurde erfolgreich bestätigt! 🎉");
                return "auth/verify";
            } else {
                model.addAttribute("error", "Der Code ist ungültig.");
                return "auth/verify";
            }

        } catch (IllegalStateException ex) {
            model.addAttribute("email", email);
            model.addAttribute("error", ex.getMessage());
            return "auth/verify";
        }
    }


    @PostMapping("/verify/resend")
    public String resend(@RequestParam String email, Model model) {
        try {
            emailVerificationService.sendCode(email);
            model.addAttribute("info", "Neuer Code wurde gesendet.");
        } catch (IllegalStateException ex) {
            model.addAttribute("error", ex.getMessage());
        }
        model.addAttribute("email", email);
        return "auth/verify";
    }
}