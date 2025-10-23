package com.example.shop.web.controller;

import com.example.shop.domain.Product;
import com.example.shop.repo.ProductRepo;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Controller
public class ProductController {

    private final ProductRepo products;

    public ProductController(ProductRepo products) {
        this.products = products;
    }

    // Details: /p/iphone-15  ODER  /product/iphone-15
    @GetMapping({"/p/{slug}", "/product/{slug}"})
    public String product(@PathVariable String slug, Model m) {
        Product p = products.findBySlug(slug)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        m.addAttribute("p", p);
        return "product"; // -> src/main/resources/templates/product_detail.html
    }
}
