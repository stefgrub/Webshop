package com.example.shop.service;

import com.example.shop.domain.Order;
import com.example.shop.events.OrderCreatedEvent;
import com.example.shop.repo.OrderRepo;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class AdminNotificationListener {

    private static final Logger log = LoggerFactory.getLogger(AdminNotificationListener.class);

    private final OrderRepo orders;
    private final AdminNotificationService adminMail;

    /**
     * Läuft NACH erfolgreichem Commit in einer NEUEN, readOnly-Transaktion,
     * damit Lazy-Collections sicher initialisiert werden können.
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public void onOrderCreated(OrderCreatedEvent event) {
        // Bei Java records -> accessor heißt orderId(), nicht getOrderId()
        Long id = event.orderId();

        orders.findById(id).ifPresentOrElse(order -> {
            // Lazy-Init anstoßen, damit das Template beim Rendern nicht aus der Session fliegt
            safeInitializeForMail(order);

            try {
                adminMail.notifyOrderCreated(order);
            } catch (Exception ex) {
                log.warn("Admin-Notification für Order {} fehlgeschlagen:", id, ex);
            }
        }, () -> log.warn("Order {} nicht gefunden – keine Admin-Notification gesendet.", id));
    }

    private void safeInitializeForMail(Order order) {
        if (order.getItems() != null) {
            order.getItems().forEach(it -> {
                if (it.getProduct() != null) {
                    // greift auf Felder zu, um Lazy-Proxies zu resolven
                    it.getProduct().getName();
                }
                it.getQuantity();
                it.getPriceAtPurchaseCents();
                it.getPriceCents();
            });
        }
        order.getTotalCents();
        order.getFullName();
    }
}