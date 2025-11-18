package com.example.shop.repo;

import com.example.shop.domain.Product;
import com.example.shop.domain.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductImageRepo extends JpaRepository<ProductImage, Long> {

    List<ProductImage> findByProductOrderBySortIndexAscIdAsc(Product product);
}