package com.example.shop.web.controller;

import com.example.shop.domain.User;
import com.example.shop.repo.UserRepo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class RegistrationController {

    private final UserRepo users;
    private final PasswordEncoder encoder;

    public RegistrationController(UserRepo users, PasswordEncoder encoder) {
        this.users = users;
        this.encoder = encoder;
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

        // 1. Prüfen, ob Bean Validation Fehler hat
        if (result.hasErrors()) {
            return "register";
        }

        // 2. Prüfen, ob Passwörter übereinstimmen
        if (!f.getPassword().equals(f.getPasswordRepeat())) {
            result.rejectValue("passwordRepeat", "error.passwordRepeat", "Passwörter stimmen nicht überein.");
            return "register";
        }

        // 3. Prüfen, ob E-Mail bereits existiert
        if (users.findByEmail(f.getEmail()).isPresent()) {
            result.rejectValue("email", "error.email", "E-Mail ist bereits registriert.");
            return "register";
        }

        // 4. Benutzer erstellen und speichern
        User u = new User();
        u.setEmail(f.getEmail());
        u.setPasswordHash(encoder.encode(f.getPassword()));
        u.setRole("USER");
        users.save(u);

        // 5. Weiterleiten zum Login
        return "redirect:/login?registered";
    }
}
