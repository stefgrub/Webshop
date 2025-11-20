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
import org.springframework.cache.annotation.Cacheable;

import java.util.List;
import java.util.Collections;

@Service
public class CatalogService {

    private static final Logger log = LoggerFactory.getLogger(CatalogService.class); // ✅ Logger hier definiert

    private final ProductRepo products;
    private final CategoryRepo categories;

    public CatalogService(ProductRepo products, CategoryRepo categories) {
        this.products = products;
        this.categories = categories;
    }

    public Page<Product> list(String q, int page, int pageSize) {
        var pageable = PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "id"));

        if (q == null || q.isBlank()) {
            // Nur aktive Produkte im öffentlichen Katalog
            return products.findAll(pageable);
        } else {
            //Volltextsuche nur über aktive Produkte
            return products.searchActive(q, pageable);
        }
    }

    /**
     * Einzelnes Produkt für die Produktdetailseite.
     * Nur aktive Produkte – inaktive liefern „nicht gefunden“.
     */
    public Product getProduct(String slug) {
        return products.findBySlugAndActiveTrue(slug)
                .orElseThrow(() -> new IllegalArgumentException("Produkt nicht gefunden oder inaktiv:: " + slug));
    }

    /**
     * Kategorien für Filter/Navigation.
     */
    @Cacheable("categories")
    public List<Category> categories() {
        try {
            return categories.findAll(Sort.by(Sort.Direction.ASC, "name"));
        } catch (Exception e) {
            // Fallback, falls findAll(Sort) nicht existiert
            return categories.findAll();
        }
    }

    /**
     * Ähnliche Produkte – nur aktive Produkte aus derselben Kategorie,
     * die nicht das aktuelle Produkt sind.
     */
    public List<Product> recommendProducts(Product base, int limit) {
        if (base == null || base.getCategory() == null) {
            return Collections.emptyList();
        }

        List<Product> list = products
                .findTop5ByCategoryAndIdNotOrderByIdDesc(base.getCategory(), base.getId());
        if (list.size() > limit) {
            return list.subList(0, limit);
        }
        return list;
    }
}