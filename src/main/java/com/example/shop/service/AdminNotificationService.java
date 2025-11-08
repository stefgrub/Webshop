package com.example.shop.service;

import com.example.shop.domain.Order;
import com.example.shop.domain.OrderItem;
import com.example.shop.repo.UserRepo;
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
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminNotificationService {

    private static final Logger log = LoggerFactory.getLogger(AdminNotificationService.class);

    private final UserRepo userRepo;
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

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

    /**
     * Wird asynchron vom AdminNotificationListener nach Bestellung aufgerufen.
     */
    public void notifyOrderCreated(Order order) {
        if (order == null) return;

        // Admin-Empfänger (nur verifizierte)
        var admins = userRepo.findAdminsVerified();
        List<String> recipients = admins.stream()
                .map(u -> u.getEmail())
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .distinct()
                .collect(Collectors.toList());

        if (recipients.isEmpty()) {
            log.warn("AdminNotificationService: Keine Admins mit gültiger E-Mail gefunden.");
            return;
        }

        String totalFormatted = formatEuro(order.getTotalCents());
        String subjectComputed = "🛒 " + brand + " – Neue Bestellung #" + order.getId() + " • " + totalFormatted;

        // HTML rendern
        String htmlComputed;
        try {
            Context ctx = new Context(Locale.GERMAN);
            ctx.setVariable("order", order);
            ctx.setVariable("items", order.getItems() != null ? order.getItems() : List.<OrderItem>of());
            ctx.setVariable("brand", brand);
            ctx.setVariable("adminUrl", baseUrl + "/admin/orders/" + order.getId());
            htmlComputed = templateEngine.process("mail/order-new-admin", ctx);
        } catch (Exception e) {
            log.warn("AdminNotificationService: Fehler beim Rendern des Templates:", e);
            htmlComputed =
                    "<p>Neue Bestellung #" + order.getId() + " – Gesamt: " + totalFormatted + "</p>" +
                            "<p><a href=\"" + baseUrl + "/admin/orders\">Zu den Admin-Bestellungen</a></p>";
        }

        String plainComputed = buildPlainText(order, totalFormatted);

        // -> final Kopien für Lambda
        final String subject = subjectComputed;
        final String htmlBody = htmlComputed;
        final String plainBody = plainComputed;
        final String fromAddr = from;
        final String fromPersonal = fromName;
        final String replyToAddr = (replyTo != null && !replyTo.isBlank()) ? replyTo : null;

        for (String to : recipients) {
            try {
                final String toFinal = to;
                MimeMessagePreparator prep = msg -> {
                    // multipart=true (Text+HTML)
                    MimeMessageHelper h = new MimeMessageHelper(msg, true, "UTF-8");
                    h.setFrom(fromAddr, fromPersonal);
                    h.setTo(toFinal);
                    if (replyToAddr != null) h.setReplyTo(replyToAddr);
                    h.setSubject(subject);
                    h.setText(plainBody, htmlBody);
                };
                mailSender.send(prep);
                log.info("Admin-Mail gesendet an {}", toFinal);
            } catch (Exception e) {
                log.warn("Admin-Mail an {} fehlgeschlagen:", to, e);
            }
        }
    }

    // -------------------------
    // Hilfsfunktionen
    // -------------------------

    private String buildPlainText(Order order, String totalFormatted) {
        StringBuilder sb = new StringBuilder();
        sb.append("Neue Bestellung #").append(order.getId()).append("\n");
        sb.append("Kunde: ").append(nullSafe(order.getFullName())).append("\n");
        sb.append("Adresse: ")
                .append(nullSafe(order.getStreet())).append(", ")
                .append(nullSafe(order.getPostalCode())).append(" ")
                .append(nullSafe(order.getCity())).append(", ")
                .append(nullSafe(order.getCountry())).append("\n\n");

        sb.append("Positionen:\n");
        if (order.getItems() != null) {
            for (OrderItem item : order.getItems()) {
                String name = (item.getProduct() != null && item.getProduct().getName() != null)
                        ? item.getProduct().getName()
                        : "Produkt";
                int qty = item.getQuantity() != null ? item.getQuantity() : 0;
                int cents = (item.getPriceAtPurchaseCents() != null)
                        ? item.getPriceAtPurchaseCents()
                        : (item.getPriceCents() != null ? item.getPriceCents() : 0);
                sb.append("- ").append(name)
                        .append(" x").append(qty)
                        .append(" @ ").append(formatEuro(cents))
                        .append("\n");
            }
        }
        sb.append("\nGesamt: ").append(totalFormatted).append("\n");
        sb.append("Admin-Link: ").append(baseUrl).append("/admin/orders/").append(order.getId()).append("\n");
        return sb.toString();
    }

    private String nullSafe(Object o) {
        return o == null ? "" : String.valueOf(o);
    }

    private String formatEuro(Integer cents) {
        if (cents == null) return "-";
        double eur = cents / 100.0;
        return NumberFormat.getCurrencyInstance(Locale.GERMANY).format(eur);
    }

    private String formatEuro(int cents) {
        double eur = cents / 100.0;
        return NumberFormat.getCurrencyInstance(Locale.GERMANY).format(eur);
    }
}