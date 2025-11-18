package com.example.shop.repo;

import com.example.shop.domain.Order;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface OrderRepo extends JpaRepository<Order, Long> {

    // Holt Order mit Items, Products und User (für Detailansicht und E-Mails)
    @EntityGraph(attributePaths = { "items", "items.product", "user" })
    Optional<Order> findWithItemsById(Long id);

    // Admin Übersicht
    List<Order> findAllByOrderByCreatedAtDesc();

    // Filter nach Status
    List<Order> findByStatusOrderByCreatedAtDesc(Order.Status status);

    // Paged nach Status
    Page<Order> findByStatus(Order.Status status, Pageable pageable);

}