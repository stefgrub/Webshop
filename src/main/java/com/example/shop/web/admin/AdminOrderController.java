package com.example.shop.web.admin;

import com.example.shop.domain.Order;
import com.example.shop.repo.OrderRepo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/orders")
public class AdminOrderController {

    private final OrderRepo orders;

    public AdminOrderController(OrderRepo orders) {
        this.orders = orders;
    }

    // 🔹 Übersicht aller Bestellungen (mit optionalem Filter)
    @GetMapping
    public String list(@RequestParam(required = false) String status, Model m) {
        List<Order> list = (status == null || status.isBlank())
                ? orders.findAll()
                : orders.findByStatus(Order.Status.valueOf(status.toUpperCase()));

        m.addAttribute("orders", list);
        m.addAttribute("selectedStatus", status);
        m.addAttribute("statuses", Order.Status.values());
        return "admin_orders";
    }

    // 🔹 Detailansicht für eine Bestellung
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model m) {
        Order o = orders.findById(id).orElseThrow();
        m.addAttribute("order", o);
        return "admin_order_detail";
    }

    // 🔹 Statusänderung (z. B. von NEW → SHIPPED)
    @PostMapping("/{id}/status")
    public String updateStatus(@PathVariable Long id, @RequestParam String status) {
        Order o = orders.findById(id).orElseThrow();
        o.setStatus(Order.Status.valueOf(status.toUpperCase()));
        orders.save(o);
        return "redirect:/admin/orders";
    }

    // 🔹 Bestellung stornieren (Soft Delete)
    @PostMapping("/cancel/{id}")
    public String cancel(@PathVariable Long id) {
        Order o = orders.findById(id).orElseThrow();
        o.setCanceled(true);
        o.setStatus(Order.Status.CANCELED);
        orders.save(o);
        return "redirect:/admin/orders";
    }

    // 🔹 Bestellung endgültig löschen (Hard Delete)
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        orders.deleteById(id);
        return "redirect:/admin/orders";
    }
}
