package com.example.shop.events;

import com.example.shop.domain.Order;

public record OrderStatusChangedEvent(Long orderId, Order.Status oldStatus, Order.Status newStatus) {}