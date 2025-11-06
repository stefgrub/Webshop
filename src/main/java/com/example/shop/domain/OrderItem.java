package com.example.shop.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "order_items")
public class OrderItem {

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

    // --- getters/setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public Integer getPriceAtPurchaseCents() { return priceAtPurchaseCents; }
    public void setPriceAtPurchaseCents(Integer priceAtPurchaseCents) { this.priceAtPurchaseCents = priceAtPurchaseCents; }

    // ---- Alias-Methoden für Kompatibilität ----
    public Integer getPriceCents() { return priceAtPurchaseCents; }
    public void setPriceCents(Integer priceCents) { this.priceAtPurchaseCents = priceCents; }
}