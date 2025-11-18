package com.example.shop.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "orders")
public class Order {

    public enum Status { NEW, PAID, SHIPPED, CANCELED }

    // --- getters/setters ---
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    @Column(name = "total_cents", nullable = false)
    private Integer totalCents = 0;

    @Enumerated(EnumType.STRING)
    private Status status = Status.NEW;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    // Lieferadresse / Rechnungsadresse (wie in deinem CheckoutService genutzt)
    @Column(name = "full_name")
    private String fullName;

    private String street;

    @Column(name = "postal_code")
    private String postalCode;

    private String city;

    private String country;

    private boolean canceled = false;

    public Order() {}

    // --- convenience ---
    public void addItem(OrderItem item) {
        if (item != null) {
            item.setOrder(this);
            this.items.add(item);
        }
    }

}