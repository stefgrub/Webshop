package com.example.shop.repo;


import com.example.shop.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;


public interface CategoryRepo extends JpaRepository<Category, Long> {
Optional<Category> findBySlug(String slug);
}