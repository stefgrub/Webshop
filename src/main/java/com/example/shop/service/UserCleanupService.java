package com.example.shop.service;

import com.example.shop.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserCleanupService {

    private final UserRepo userRepo;

    // Läuft jeden Tag um 3:30 Uhr
    @Scheduled(cron = "0 30 3 * * *")
    @Transactional
    public void cleanupOldUnverifiedUsers() {
        Instant threshold = Instant.now().minus(7, ChronoUnit.DAYS); // z.B. älter als 7 Tage
        int deleted = userRepo.deleteUnverifiedOlderThan(threshold);
        if (deleted > 0) {
            log.info("UserCleanupService: {} unverifizierte Accounts gelöscht.", deleted);
        }
    }
}