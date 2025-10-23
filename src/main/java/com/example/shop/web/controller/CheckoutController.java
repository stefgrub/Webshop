package com.example.shop.web.controller;

import com.example.shop.domain.Order;
import com.example.shop.domain.Product;
import com.example.shop.repo.ProductRepo;
import com.example.shop.service.Cart;
import com.example.shop.service.CheckoutService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@SessionAttributes("cart")
public class CheckoutController {

    private final CheckoutService checkout;
    private final ProductRepo products;

    public CheckoutController(CheckoutService checkout, ProductRepo products) {
        this.checkout = checkout;
        this.products = products;
    }

    @GetMapping("/checkout")
    public String form(@ModelAttribute("cart") Cart cart, Model m) {
        if (cart == null || cart.isEmpty()) return "redirect:/cart";

        // ✅ Produkte aus Warenkorb laden
        Map<Product, Integer> lines = new HashMap<>();
        int total = 0;
        for (Map.Entry<Long, Integer> e : cart.getItems().entrySet()) {
            Product p = products.findById(e.getKey()).orElseThrow();
            lines.put(p, e.getValue());
            total += p.getPriceCents() * e.getValue();
        }

        m.addAttribute("lines", lines);
        m.addAttribute("totalCents", total);
        return "checkout";
    }

    @PostMapping("/checkout")
    public String submit(@AuthenticationPrincipal User principal,
                         @ModelAttribute("cart") Cart cart,
                         @RequestParam String fullName,
                         @RequestParam String street,
                         @RequestParam String postalCode,
                         @RequestParam String city,
                         @RequestParam String country) {
        if (cart == null || cart.isEmpty()) return "redirect:/cart";

        Order order = checkout.placeOrder(
                principal.getUsername(), cart,
                fullName, street, postalCode, city, country
        );
        return "redirect:/orders/" + order.getId();
    }
}
