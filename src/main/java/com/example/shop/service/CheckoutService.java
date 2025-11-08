package com.example.shop.service;

import com.example.shop.domain.Order;
import com.example.shop.domain.OrderItem;
import com.example.shop.domain.Product;
import com.example.shop.domain.User;
import com.example.shop.events.OrderCreatedEvent;
import com.example.shop.repo.OrderRepo;
import com.example.shop.repo.ProductRepo;
import com.example.shop.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CheckoutService {

    private static final Logger log = LoggerFactory.getLogger(CheckoutService.class);

    private final OrderRepo orders;
    private final ProductRepo products;
    private final UserRepo users;
    private final AdminNotificationService adminNotifications; // ok für Admin-Mails via eigenem Listener
    private final ApplicationEventPublisher events;

    @Transactional
    public Order placeOrder(String username, Cart cart,
                            String fullName, String street, String postalCode, String city, String country) {

        if (cart == null || cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new IllegalArgumentException("Warenkorb ist leer.");
        }

        // Benutzer lookup (case-insensitiv, Fallback auf exakt)
        Optional<User> userOpt = Optional.empty();
        if (username != null && !username.isBlank()) {
            userOpt = users.findByEmailIgnoreCase(username)
                    .or(() -> users.findByEmail(username));
        }

        // Bestellung aufbauen
        Order order = new Order();
        userOpt.ifPresent(order::setUser);
        order.setStatus(Order.Status.NEW);
        order.setCreatedAt(OffsetDateTime.now());
        order.setFullName(fullName);
        order.setStreet(street);
        order.setPostalCode(postalCode);
        order.setCity(city);
        order.setCountry(country);

        long total = 0L;

        for (Map.Entry<Long, Integer> e : cart.getItems().entrySet()) {
            Long productId = e.getKey();
            int qty = e.getValue() == null ? 0 : e.getValue();
            if (qty <= 0) continue;

            Product p = products.findById(productId)
                    .orElseThrow(() -> new IllegalArgumentException("Produkt nicht gefunden: id=" + productId));

            Integer unitCents = p.getPriceCents();
            if (unitCents == null) {
                throw new IllegalStateException("Preis (Cents) ist null für Produkt id=" + productId);
            }

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProduct(p);
            item.setQuantity(qty);

            if (hasPriceAtPurchaseField(item)) {
                item.setPriceAtPurchaseCents(unitCents);
            } else {
                item.setPriceCents(unitCents);
            }

            order.addItem(item);
            total = Math.addExact(total, Math.multiplyExact(unitCents.longValue(), (long) qty));

            // Optional: Lager anpassen (wenn gewünscht)
            // if (p.getStock() != null) p.setStock(Math.max(0, p.getStock() - qty));
        }

        order.setTotalCents(Math.toIntExact(total));
        orders.save(order);
        cart.clear();

        log.info("Bestellung erfolgreich angelegt ({} Positionen, Gesamt: {} Cent)",
                order.getItems().size(), total);

        // 🔔 Event: löst Admin- UND Kundenbestätigung aus (über Listener)
        events.publishEvent(new OrderCreatedEvent(order.getId()));

        return order;
    }

    private boolean hasPriceAtPurchaseField(OrderItem item) {
        try {
            item.getClass().getMethod("setPriceAtPurchaseCents", Integer.class);
            return true;
        } catch (NoSuchMethodException ex) {
            return false;
        }
    }
}