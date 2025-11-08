// Nur wenn du unbedingt einen separaten Listener willst – sonst Datei löschen!
package com.example.shop.service;

import com.example.shop.events.OrderCreatedEvent;
import com.example.shop.events.OrderStatusChangedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;

@Component
@RequiredArgsConstructor
public class CustomerNotificationListener {

    private final CustomerNotificationService customer;

    @Async
    @TransactionalEventListener(phase = AFTER_COMMIT)
    public void onOrderCreated(OrderCreatedEvent ev) {
        // CustomerNotificationService verarbeitet selbst via eigenen @TransactionalEventListener
        // -> Wenn du diesen Listener nutzt, ruf einfach eine öffentliche Methode auf:
        // customer.sendCreatedMail(ev.orderId());
        // (Methodenname muss natürlich im Service existieren; standardmäßig NICHT nötig)
    }

    @Async
    @TransactionalEventListener(phase = AFTER_COMMIT)
    public void onStatusChanged(OrderStatusChangedEvent ev) {
        // customer.sendStatusMail(ev.orderId(), ev.oldStatus(), ev.newStatus());
        // (nur wenn du so eine Methode im Service anbietest)
    }
}