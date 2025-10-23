package com.example.shop.repo;// ProductRepo.java
import com.example.shop.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepo extends JpaRepository<Product, Long> {
    Optional<Product> findBySlug(String slug);

    // <-- NEU:
    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);

    // (falls vorhanden) Suche in Name ODER Beschreibung:
    Page<Product> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
            String q1, String q2, Pageable pageable
    );
}
