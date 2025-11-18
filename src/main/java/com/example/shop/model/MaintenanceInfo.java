package com.example.shop.model;

import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Setter
@Getter
public class MaintenanceInfo {

    private boolean active;
    private String message;
    private OffsetDateTime start;
    private OffsetDateTime end;

    public MaintenanceInfo() {}

    public MaintenanceInfo(boolean active, String message, OffsetDateTime start, OffsetDateTime end) {
        this.active = active;
        this.message = message;
        this.start = start;
        this.end = end;
    }

    @Override public String toString() {
        return "MaintenanceInfo{active=" + active + ", message='" + message + "', start=" + start + ", end=" + end + '}';
    }
}