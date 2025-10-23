package com.example.shop.repo;

import com.example.shop.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderRepo extends JpaRepository<Order, Long> {

    List<Order> findByStatus(Order.Status status);
}
