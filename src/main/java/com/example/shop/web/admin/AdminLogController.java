package com.example.shop.web.admin;

import com.example.shop.service.AuditLogService;
import org.springframework.data.domain.Page;
import com.example.shop.domain.AuditLog;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/logs")
public class AdminLogController {

    private final AuditLogService service;

    public AdminLogController(AuditLogService service) {
        this.service = service;
    }

    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "25") int size,
                       Model model) {
        Page<AuditLog> p = service.page(page, size);
        model.addAttribute("page", p);
        return "admin/logs";
    }
}