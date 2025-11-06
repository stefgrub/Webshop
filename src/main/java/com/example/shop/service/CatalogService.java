package com.example.shop.service;

import com.example.shop.domain.Category;
import com.example.shop.domain.Product;
import com.example.shop.repo.CategoryRepo;
import com.example.shop.repo.ProductRepo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Service
public class CatalogService {

    private static final Logger log = LoggerFactory.getLogger(CatalogService.class); // ✅ Logger hier definiert

    private final ProductRepo products;
    private final CategoryRepo categories;

    public CatalogService(ProductRepo products, CategoryRepo categories) {
        this.products = products;
        this.categories = categories;
    }

    public Page<Product> list(String q, int page, int size) {
        var pageable = PageRequest.of(page, size, Sort.by("id").descending());
        if (q == null || q.isBlank()) {
            log.debug("Alle Produkte werden geladen (ohne Suchbegriff)");
            return products.findAll(pageable);
        }
        log.debug("Produkte mit Filter '{}' werden geladen", q);
        return products.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(q, q, pageable);
    }

    public List<Category> categories() {
        log.debug("Lade Kategorien aus der Datenbank");
        return categories.findAll(Sort.by("name").ascending());
    }
}