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
    }

    @Async
    @TransactionalEventListener(phase = AFTER_COMMIT)
    public void onStatusChanged(OrderStatusChangedEvent ev) {
    }
}