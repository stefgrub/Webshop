package com.example.shop.web;

import com.example.shop.domain.Order;
import com.example.shop.repo.OrderRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/orders")
public class CustomerOrderController {

    private final OrderRepo orders;

    @GetMapping("/{id}")
    public String view(@PathVariable Long id,
                       @AuthenticationPrincipal UserDetails me,
                       Model model) {
        Order o = orders.findById(id).orElseThrow();

        // Minimal-Absicherung: nur Besitzer (oder keine Zuordnung)
        if (o.getUser() != null && me != null && !o.getUser().getEmail().equalsIgnoreCase(me.getUsername())) {
            // Optional: 404 statt 403, je nach Geschmack
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.FORBIDDEN, "Not your order");
        }

        model.addAttribute("order", o);
        return "order"; // dein Template order.html
    }
}