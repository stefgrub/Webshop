package com.example.shop.service;

import com.example.shop.domain.Order;
import com.example.shop.domain.OrderItem;
import com.example.shop.domain.Product;
import com.example.shop.domain.User;
import com.example.shop.repo.OrderRepo;
import com.example.shop.repo.ProductRepo;
import com.example.shop.repo.UserRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.OffsetDateTime;
import java.util.Map;

@Service
public class CheckoutService {

    private static final Logger log = LoggerFactory.getLogger(CheckoutService.class); // ✅ Logger hier definiert

    private final OrderRepo orders;
    private final ProductRepo products;
    private final UserRepo users;

    public CheckoutService(OrderRepo orders, ProductRepo products, UserRepo users) {
        this.orders = orders;
        this.products = products;
        this.users = users;
    }

    @Transactional
    public Order placeOrder(String userEmail, Cart cart, String fullName, String street,
                            String postal, String city, String country) {

        log.info("Starte Checkout-Prozess für Benutzer {}", userEmail);

        User user = users.findByEmail(userEmail).orElseThrow();

        Order order = new Order();
        order.setUser(user);
        order.setStatus(Order.Status.NEW);
        order.setCreatedAt(OffsetDateTime.now());
        order.setFullName(fullName);
        order.setStreet(street);
        order.setPostalCode(postal);
        order.setCity(city);
        order.setCountry(country);

        int total = 0;
        for (Map.Entry<Long, Integer> e : cart.getItems().entrySet()) {
            Product p = products.findById(e.getKey()).orElseThrow();
            int qty = e.getValue();
            int line = p.getPriceCents() * qty;
            total += line;

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProduct(p);
            item.setQuantity(qty);
            item.setPriceAtPurchaseCents(p.getPriceCents());
            order.getItems().add(item);

            Integer stock = p.getStock() == null ? 0 : p.getStock();
            int newStock = Math.max(0, stock - qty);
            p.setStock(newStock);
        }

        order.setTotalCents(total);
        orders.save(order);
        cart.clear();

        log.info("Bestellung erfolgreich angelegt ({} Positionen, Gesamt: {} Cent)",
                order.getItems().size(), total);

        return order;
    }
}