package com.example.shop.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import java.time.OffsetDateTime;

@Setter
@Getter
@ConfigurationProperties(prefix = "maintenance")
public class MaintenanceProps {
    private boolean enabled;
    private OffsetDateTime end;
    private String message;
    private boolean homepageOnly;

}