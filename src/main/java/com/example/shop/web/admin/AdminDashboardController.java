package com.example.shop.web.admin;

import com.example.shop.repo.AnnouncementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class AdminDashboardController {

    private final AnnouncementRepository announcements;

    @GetMapping("/admin/home")
    public String altdashboard(Model model) {
        model.addAttribute("announcements",
                announcements.findByActiveTrueOrderByLevelDescUpdatedAtDesc());
        return "admin/dashboard";
    }
}