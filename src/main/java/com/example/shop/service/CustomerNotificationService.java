package com.example.shop.service;

import com.example.shop.domain.Order;
import com.example.shop.domain.OrderItem;
import com.example.shop.repo.OrderRepo;
import jakarta.mail.internet.InternetAddress;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.text.NumberFormat;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class CustomerNotificationService {

    private static final Logger log = LoggerFactory.getLogger(CustomerNotificationService.class);

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;
    private final OrderRepo orders;

    @Value("${app.mail.from:support@viewwebshop.at}")
    private String from;

    @Value("${app.mail.from-name:ViewWebshop}")
    private String fromName;

    @Value("${app.mail.reply-to:support@viewwebshop.at}")
    private String replyTo;

    @Value("${app.base-url:https://viewwebshop.at}")
    private String baseUrl;

    @Value("${app.brand.name:ViewWebshop}")
    private String brand;

    /** 🔹 Nach erfolgreicher Bestellung */
    public void sendOrderConfirmation(Long orderId) {
        var order = orders.findWithItemsById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));
        if (order.getUser() == null || order.getUser().getEmail() == null) return;

        String subject = "🛍️ " + brand + " – Danke für deine Bestellung #" + order.getId();
        Context ctx = new Context(Locale.GERMAN);
        ctx.setVariable("order", order);
        ctx.setVariable("brand", brand);
        ctx.setVariable("baseUrl", baseUrl);
        String html = templateEngine.process("mail/order-customer-created", ctx);

        MimeMessagePreparator prep = msg -> {
            MimeMessageHelper h = new MimeMessageHelper(msg, true, "UTF-8");
            h.setFrom(new InternetAddress(from, fromName));
            h.setTo(order.getUser().getEmail());
            h.setReplyTo(replyTo);
            h.setSubject(subject);
            h.setText(html, true);
        };
        try {
            mailSender.send(prep);
            log.info("Kunden-Mail versendet: Bestellung {}", orderId);
        } catch (Exception e) {
            log.warn("Kunden-Mail fehlgeschlagen: {}", e.getMessage());
        }
    }

    /** 🔹 Bei Statusänderung */
    public void sendStatusChanged(Long orderId, Order.Status oldStatus, Order.Status newStatus) {
        var order = orders.findWithItemsById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));
        if (order.getUser() == null || order.getUser().getEmail() == null) return;

        String subject = "📦 " + brand + " – Statusaktualisierung deiner Bestellung #" + order.getId();
        Context ctx = new Context(Locale.GERMAN);
        ctx.setVariable("order", order);
        ctx.setVariable("oldStatus", oldStatus);
        ctx.setVariable("newStatus", newStatus);
        ctx.setVariable("brand", brand);
        ctx.setVariable("baseUrl", baseUrl);
        String html = templateEngine.process("mail/order-customer-status", ctx);

        MimeMessagePreparator prep = msg -> {
            MimeMessageHelper h = new MimeMessageHelper(msg, true, "UTF-8");
            h.setFrom(new InternetAddress(from, fromName));
            h.setTo(order.getUser().getEmail());
            h.setReplyTo(replyTo);
            h.setSubject(subject);
            h.setText(html, true);
        };
        try {
            mailSender.send(prep);
            log.info("Kunden-Mail (Statusänderung) versendet: Bestellung {}", orderId);
        } catch (Exception e) {
            log.warn("Kunden-Mail (Statusänderung) fehlgeschlagen: {}", e.getMessage());
        }
    }
}