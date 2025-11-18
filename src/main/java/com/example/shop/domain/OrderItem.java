package com.example.shop.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "order_items")
public class OrderItem {

    // --- getters/setters ---
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(optional = false)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "price_at_purchase_cents", nullable = false)
    private Integer priceAtPurchaseCents;

    public OrderItem() {}

    // ---- Alias-Methoden für Kompatibilität ----
    public Integer getPriceCents() { return priceAtPurchaseCents; }
    public void setPriceCents(Integer priceCents) { this.priceAtPurchaseCents = priceCents; }
}