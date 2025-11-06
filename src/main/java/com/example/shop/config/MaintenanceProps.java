package com.example.shop.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import java.time.OffsetDateTime;

@ConfigurationProperties(prefix = "maintenance")
public class MaintenanceProps {
    private boolean enabled;
    private OffsetDateTime end;
    private String message;
    private boolean homepageOnly;

    public boolean isEnabled() {
        return enabled;
    }
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    public OffsetDateTime getEnd() {
        return end;
    }
    public void setEnd(OffsetDateTime end) {
        this.end = end;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public boolean isHomepageOnly() {
        return homepageOnly;
    }
    public void setHomepageOnly(boolean homepageOnly) {
        this.homepageOnly = homepageOnly;
    }
}