package com.example.shop.service;

import com.example.shop.repo.ProductRepo;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {

    private final ProductRepo products;

    public ProductService(ProductRepo products) {
        this.products = products;
    }

    @Transactional
    public void deleteProductById(Long id) {

        if (!products.existsById(id)) {
            throw new IllegalArgumentException("product not found: " + id);
        }
        try {
            products.deleteById(id);
            products.flush();
        } catch (DataIntegrityViolationException e) {

            throw new ProductInUseException("product is referenced and cannot be deleted", e);
        }
    }
}