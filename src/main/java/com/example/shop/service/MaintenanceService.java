package com.example.shop.service;

import com.example.shop.model.MaintenanceInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
public class MaintenanceService {
    private static final Logger log = LoggerFactory.getLogger(MaintenanceService.class);

    private MaintenanceInfo current = new MaintenanceInfo(false, "Keine Wartung aktiv", null, null);

    public MaintenanceInfo getCurrentMaintenance() {
        return current;
    }

    public void activate(String message, OffsetDateTime end) {
        current = new MaintenanceInfo(true, message, OffsetDateTime.now(), end);
        log.info("Maintenance aktiviert: {} bis {}", message, end);
    }

    public void deactivate() {
        current = new MaintenanceInfo(false, "Keine Wartung aktiv", null, null);
        log.info("Maintenance deaktiviert");
    }
}