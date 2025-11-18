package com.example.shop.web.admin;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import com.example.shop.domain.Order;
import com.example.shop.repo.OrderRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {

    private final OrderRepo orders;

    // Übersicht (optional nach Status filtern)
    @GetMapping
    public String list(@RequestParam(name = "status", required = false) Order.Status selectedStatus,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "20") int size,
                       Model model) {

        // PageRequest: sortiere nach createdAt DESC
        PageRequest pr = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Order> p = (selectedStatus == null)
                ? orders.findAll(pr)
                : orders.findByStatus(selectedStatus, pr);

        // Für das Template:
        model.addAttribute("orders", p.getContent()); // wie bisher
        model.addAttribute("page", p);                // für Pagination-Fragment
        model.addAttribute("selectedStatus", selectedStatus);
        model.addAttribute("statuses", Order.Status.values());

        return "admin/orders";
    }

    // Detailansicht
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Order o = orders.findById(id).orElseThrow();
        model.addAttribute("order", o);
        return "admin/order_detail";
    }

    // ✅ JSON: wird von /js/orders.js genutzt (AJAX)
    @PostMapping(path = "/{id}/status",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Object> updateStatusJson(@PathVariable Long id,
                                                @RequestBody Map<String, String> body) {
        String statusStr = body.getOrDefault("status", "NEW");
        Order.Status status = parseStatus(statusStr);

        Order o = orders.findById(id).orElseThrow();
        o.setStatus(status);
        orders.save(o);

        return Map.of("ok", true, "id", id, "status", status.name());
    }

    // ✅ FORM: wird von admin/order_detail.html genutzt (klassischer POST)
    @PostMapping(path = "/{id}/status", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String updateStatusForm(@PathVariable Long id,
                                   @RequestParam("status") String statusStr) {
        Order.Status status = parseStatus(statusStr);
        Order o = orders.findById(id).orElseThrow();
        o.setStatus(status);
        orders.save(o);

        return "redirect:/admin/orders/" + id + "?updated=1";
    }

    // Bestellung endgültig löschen (AJAX)
    @PostMapping(path = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Object> deleteJson(@PathVariable Long id) {
        orders.deleteById(id);
        return Map.of("ok", true, "id", id);
    }

    // (Optional) Legacy-Form-Variante fürs Stornieren
    @PostMapping("/cancel/{id}")
    public String cancelLegacy(@PathVariable Long id) {
        Order o = orders.findById(id).orElseThrow();
        o.setCanceled(true);
        o.setStatus(Order.Status.CANCELED);
        orders.save(o);
        return "redirect:/admin/orders";
    }

    // Helper
    private Order.Status parseStatus(String s) {
        try {
            return Order.Status.valueOf(s == null ? "NEW" : s.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            return Order.Status.NEW;
        }
    }
}