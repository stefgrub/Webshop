package com.example.shop.web;

import com.example.shop.config.MaintenanceProps;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import java.time.OffsetDateTime;
import java.util.Map;

@ControllerAdvice
public class GlobalModel {
    private final MaintenanceProps props;
    public GlobalModel(MaintenanceProps props){ this.props = props; }

    @ModelAttribute("maintenance")
    public Map<String,Object> maintenance(){
        boolean active = props.isEnabled()
                && props.getEnd() != null
                && OffsetDateTime.now().isBefore(props.getEnd());
        return Map.of(
                "active", active,
                "message", props.getMessage(),
                "end", props.getEnd(),
                "homepageOnly", props.isHomepageOnly()
        );
    }
}