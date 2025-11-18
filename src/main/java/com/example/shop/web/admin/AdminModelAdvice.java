package com.example.shop.web.admin;

import com.example.shop.settings.SettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice(assignableTypes = {
        AdminDashboardController.class,
//      AdminOrderController.class, AdminProductController.class, AdminUserController.class
})
@RequiredArgsConstructor
public class AdminModelAdvice {

    private final SettingsService settings;

    @ModelAttribute("featureCheckoutUiEnabled")
    public boolean featureCheckoutUiEnabled() {
        return settings.getBool("feature.checkout.ui", false);
    }
}