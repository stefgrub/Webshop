package com.example.shop.web.admin;

import com.example.shop.service.AdminAiService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/assistant")
public class AdminAiController {

    private final AdminAiService adminAiService;

    @GetMapping
    public String viewAssistant(
            @RequestParam(value = "q", required = false) String q,
            Authentication auth,
            Model model
    ) {
        String answer = null;

        // Nur wenn eine Frage gestellt wurde
        if (q != null && !q.isBlank()) {
            String adminUser = (auth != null ? auth.getName() : "unbekannt");
            answer = adminAiService.askAssistant(q, adminUser);
        }

        model.addAttribute("question", q);
        model.addAttribute("answer", answer);
        return "admin/assistant";  // -> templates/admin/assistant.html
    }

    @PostMapping
    public String ask(@RequestParam("q") String q) {
        // Redirect (PRG Pattern), damit Reload kein erneutes POST macht
        return "redirect:/admin/assistant?q=" +
                URLEncoder.encode(q, StandardCharsets.UTF_8);
    }
}