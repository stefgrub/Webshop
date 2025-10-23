package com.example.shop.web.controller;

import com.example.shop.domain.Product;
import com.example.shop.repo.ProductRepo;
import com.example.shop.service.Cart;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

@Controller
@SessionAttributes("cart")
public class CartController {

    private final ProductRepo products;

    public CartController(ProductRepo products) {
        this.products = products;
    }

    @ModelAttribute("cart")
    public Cart initCart() {
        return new Cart(); // Session-Warenkorb
    }

    @PostMapping("/cart/add/{id}")
    public String addToCart(@PathVariable Long id,
                            @RequestParam(defaultValue = "1") int qty,
                            @ModelAttribute("cart") Cart cart,
                            RedirectAttributes ra) {
        var product = products.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        // 🔒 Sicherheitsprüfung: Produkt auf Lager?
        if (product.getStock() <= 0) {
            ra.addFlashAttribute("error", "Das Produkt \"" + product.getName() + "\" ist leider nicht mehr auf Lager.");
            return "redirect:/";
        }

        // 🔒 Sicherheitsprüfung: Wird mehr angefordert als vorhanden?
        if (qty > product.getStock()) {
            ra.addFlashAttribute("error", "Es sind nur " + product.getStock() + " Stück von \"" + product.getName() + "\" verfügbar.");
            return "redirect:/";
        }

        cart.add(product.getId(), qty);
        ra.addFlashAttribute("toast", "\"" + product.getName() + "\" wurde zum Warenkorb hinzugefügt.");
        return "redirect:/cart";
    }

    @PostMapping("/cart/update/{id}")
    public String update(@PathVariable Long id,
                         @RequestParam int qty,
                         @ModelAttribute("cart") Cart cart,
                         RedirectAttributes ra) {
        var product = products.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (qty > product.getStock()) {
            ra.addFlashAttribute("error", "Maximal " + product.getStock() + " Stück von \"" + product.getName() + "\" sind verfügbar.");
            return "redirect:/cart";
        }

        cart.update(id, qty);
        return "redirect:/cart";
    }

    @GetMapping("/cart")
    public String view(@ModelAttribute("cart") Cart cart, Model model) {
        Map<Product, Integer> lines = new HashMap<>();
        for (Map.Entry<Long, Integer> e : cart.getItems().entrySet()) {
            Product p = products.findById(e.getKey()).orElseThrow();
            lines.put(p, e.getValue());
        }

        int total = lines.entrySet().stream()
                .mapToInt(e -> e.getKey().getPriceCents() * e.getValue())
                .sum();

        model.addAttribute("lines", lines);
        model.addAttribute("totalCents", total);
        return "cart";
    }

    @PostMapping("/cart/clear")
    public String clear(@ModelAttribute("cart") Cart cart, SessionStatus status) {
        cart.clear();
        status.setComplete();
        return "redirect:/cart";
    }
}
