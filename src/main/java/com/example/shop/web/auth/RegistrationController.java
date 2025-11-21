package com.example.shop.web.auth;

import com.example.shop.config.RecaptchaProperties;
import com.example.shop.domain.User;
import com.example.shop.repo.UserRepo;
import com.example.shop.service.EmailVerificationService;
import com.example.shop.service.RecaptchaService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
public class RegistrationController {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final EmailVerificationService emailVerificationService;
    private final RecaptchaService recaptchaService;
    private final RecaptchaProperties recaptchaProperties;

    public RegistrationController(UserRepo userRepo,
                                  PasswordEncoder passwordEncoder,
                                  EmailVerificationService emailVerificationService,
                                  RecaptchaService recaptchaService,
                                  RecaptchaProperties recaptchaProperties) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.emailVerificationService = emailVerificationService;
        this.recaptchaService = recaptchaService;
        this.recaptchaProperties = recaptchaProperties;
    }

    // ---------- DTO für das Formular ----------
    public static class RegistrationForm {
        @NotBlank
        @Email
        private String email;

        @NotBlank
        @Size(min = 8, max = 100)
        private String password;

        @NotBlank
        private String passwordRepeat;

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }

        public String getPasswordRepeat() { return passwordRepeat; }
        public void setPasswordRepeat(String passwordRepeat) { this.passwordRepeat = passwordRepeat; }
    }

    // ---------- GET /register ----------
    @GetMapping("/register")
    public String showForm(Model model) {
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", new RegistrationForm());
        }
        return "register";
    }

    // ---------- POST /register ----------
    @PostMapping("/register")
    public String handleRegister(@Valid @ModelAttribute("form") RegistrationForm form,
                                 BindingResult binding,
                                 @RequestParam(name = "g-recaptcha-response", required = false) String recaptchaResponse,
                                 HttpServletRequest request,
                                 Model model) {

        String ip = request.getRemoteAddr();

        // reCAPTCHA nur prüfen, wenn aktiviert
        if (recaptchaProperties.isEnabled()) {
            boolean captchaOk = recaptchaService.isCaptchaValid(recaptchaResponse, ip);
            if (!captchaOk) {
                binding.reject("captcha.invalid", "Bitte bestätige, dass du kein Roboter bist.");
            }
        }

        // Passwort-Wiederholung prüfen
        if (!binding.hasFieldErrors("passwordRepeat") &&
                !form.getPassword().equals(form.getPasswordRepeat())) {
            binding.rejectValue("passwordRepeat", "password.mismatch", "Passwörter stimmen nicht überein.");
        }

        if (binding.hasErrors()) {
            return "register";
        }

        String email = form.getEmail().trim().toLowerCase();
        Optional<User> existingOpt = userRepo.findByEmailIgnoreCase(email);

        if (existingOpt.isPresent()) {
            User existing = existingOpt.get();
            if (existing.isEmailVerified()) {
                binding.rejectValue("email", "email.in.use", "Diese E-Mail-Adresse ist bereits registriert.");
                return "register";
            } else {
                // Account existiert, aber noch nicht verifiziert → neuen Code schicken (mit Rate-Limit in EmailVerificationService)
                try {
                    emailVerificationService.sendCode(existing);
                    model.addAttribute("email", existing.getEmail());
                    model.addAttribute("info", "Wir haben dir einen neuen Bestätigungscode geschickt.");
                    return "auth/verify";
                } catch (IllegalStateException ex) {
                    // z.B. zu viele Versuche / Rate-Limit
                    binding.reject("emailVerification", ex.getMessage());
                    return "register";
                }
            }
        }

        // Neuen (noch nicht verifizierten) User anlegen
        User user = new User();
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(form.getPassword()));
        user.setRole("USER");
        user.setEmailVerified(false);
        user.setVerificationCodeHash(null);
        user.setVerificationExpires(null);
        user.setVerificationAttempts(0);
        user.setLastCodeSent(null);
        user = userRepo.save(user);


        try {
            // 6-stelligen Code generieren & per Mail senden (mit Rate-Limit in EmailVerificationService)
            emailVerificationService.sendCode(user);
        } catch (IllegalStateException ex) {
            // Mail / Code-Versand schlägt fehl → nicht 500, sondern sauber auf der Seite anzeigen
            binding.reject("emailVerification", ex.getMessage());
            return "register";
        }

        model.addAttribute("email", user.getEmail());
        return "auth/verify";
    }

    // ---------- GET /verify ----------
    @GetMapping("/verify")
    public String showVerify(@RequestParam("email") String email, Model model) {
        model.addAttribute("email", email);
        return "auth/verify";
    }

    // ---------- POST /verify ----------
    @PostMapping("/verify")
    public String verify(@RequestParam("email") String email,
                         @RequestParam("code") String code,
                         Model model) {

        try {
            emailVerificationService.verify(email, code);
            model.addAttribute("success", "E-Mail wurde erfolgreich bestätigt. Du wirst gleich zum Login weitergeleitet.");
        } catch (IllegalStateException ex) {
            model.addAttribute("error", ex.getMessage());
        }

        model.addAttribute("email", email);
        return "auth/verify";
    }

    // ---------- POST /verify/resend ----------
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