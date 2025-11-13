package com.example.shop.web;

import com.example.shop.repo.AnnouncementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalAnnouncementAdvice {

    private final AnnouncementRepository repo;

    @ModelAttribute("announcements")
    public Object announcements() {
        // bevorzugt diese (war in deinem Code):
        try {
            return repo.findAllByActiveTrueOrderByLevelDescUpdatedAtDesc();
        } catch (Throwable ignored) {
            // Fallback auf alternative Signatur, falls nur die existiert
            return repo.findByActiveTrueOrderByLevelDescUpdatedAtDesc();
        }
    }
}