package com.example.shop.web.admin;

import com.example.shop.repo.AuditLogRepo;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin/logs")
public class AdminLogController {
    private final AuditLogRepo logs;
    public AdminLogController(AuditLogRepo logs) { this.logs = logs; }

    @GetMapping
    public String list(@RequestParam(value="q", required=false) String q,
                       @PageableDefault(size=50, sort="createdAt",
                               direction = org.springframework.data.domain.Sort.Direction.DESC)
                       Pageable pageable,
                       Model m) {
        var page = (q==null || q.isBlank())
                ? logs.findAll(pageable)
                : logs.findByEntityTypeContainingIgnoreCaseOrActionContainingIgnoreCaseOrAdminUsernameContainingIgnoreCase(q,q,q,pageable);
        m.addAttribute("page", page);
        m.addAttribute("q", q);
        return "admin_logs";
    }
}