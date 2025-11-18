package com.example.shop.web.admin;

import com.example.shop.domain.User;
import com.example.shop.repo.UserRepo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;

@Controller
@RequestMapping("/admin/users")
public class AdminUserController {

    private final UserRepo users;
    private final SessionRegistry sessionRegistry;

    public AdminUserController(UserRepo users, SessionRegistry sessionRegistry) {
        this.users = users;
        this.sessionRegistry = sessionRegistry;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("users", users.findAll());
        return "admin_users";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        User user = users.findById(id).orElseThrow();
        model.addAttribute("user", user);
        return "admin_user_form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @RequestParam String role) {
        User user = users.findById(id).orElseThrow();
        user.setRole(role.toUpperCase());
        users.save(user);

        expireSessionsForUser(user.getEmail());
        return "redirect:/admin/users";
    }

    private void expireSessionsForUser(String username) {
        sessionRegistry.getAllPrincipals().forEach(principal -> {
            if (principal instanceof UserDetails ud && ud.getUsername().equals(username)) {
                for (SessionInformation si : sessionRegistry.getAllSessions(principal, false)) {
                    si.expireNow(); // Markiert Session als abgelaufen
                }
            }
        });
    }


    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        users.deleteById(id);
        return "redirect:/admin/users";
    }
}