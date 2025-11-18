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
        String trimmed = q.trim();
        log.debug("Produkte mit Filter '{}' werden geladen", trimmed);
        return products.search(trimmed, pageable);
    }

    public Product getProduct(String slug) {
        return products.findBySlug(slug)
                .orElseThrow(() -> new IllegalArgumentException("product not found: " + slug));
    }

    @Cacheable("categories")
    public List<Category> categories() {
        log.debug("Lade Kategorien aus der Datenbank (ggf. aus dem Cache)");
        return categories.findAll(Sort.by("name").ascending());
    }

    public List<Product> recommendProducts(Product base, int limit) {
        if (base.getCategory() == null) {
            log.debug("Keine Kategorie für Produkt {}, keine Empfehlungen", base.getId());
            return List.of();
        }

        List<Product> all = products.findTop5ByCategoryAndIdNotOrderByIdDesc(base.getCategory(), base.getId());
        if (all.size() <= limit) {
            return all;
        }
        return all.subList(0, limit);
    }
}