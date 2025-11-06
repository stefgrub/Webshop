package com.example.shop.web.controller;

import com.example.shop.domain.Order;
import com.example.shop.repo.OrderRepo;
import com.example.shop.repo.UserRepo;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/orders")
public class OrderController {
    private final OrderRepo orders;
    private final UserRepo users;

    public OrderController(OrderRepo orders, UserRepo users) {
        this.orders = orders; this.users = users;
    }

    @GetMapping("/{id}")
    public String show(@PathVariable Long id,
                       @AuthenticationPrincipal User principal,
                       Model m) {
        // ✅ Falls nicht eingeloggt, zurück zur Anmeldung
        if (principal == null) {
            return "redirect:/login";
        }

        Order o = orders.findById(id).orElseThrow();
        var current = users.findByEmail(principal.getUsername()).orElseThrow();

        boolean isOwner = o.getUser().getId().equals(current.getId());
        boolean isAdmin = principal.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isOwner && !isAdmin) return "redirect:/";

        m.addAttribute("order", o);
        return "order";
    }

}