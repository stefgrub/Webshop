package com.example.shop.model;

import java.time.OffsetDateTime;

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

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public OffsetDateTime getStart() { return start; }
    public void setStart(OffsetDateTime start) { this.start = start; }

    public OffsetDateTime getEnd() { return end; }
    public void setEnd(OffsetDateTime end) { this.end = end; }

    @Override public String toString() {
        return "MaintenanceInfo{active=" + active + ", message='" + message + "', start=" + start + ", end=" + end + '}';
    }
}