package com.example.shop.web.controller;

import com.example.shop.domain.Product;
import com.example.shop.service.CatalogService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

// CatalogController.java
@Controller
@RequestMapping("/") // statt "/catalog"
public class CatalogController {

    private final CatalogService catalog;

    public CatalogController(CatalogService catalog) { this.catalog = catalog; }

    @GetMapping
    public String index(@RequestParam(defaultValue = "") String q,
                        @RequestParam(defaultValue = "0") int page,
                        Model model) {
        int pageSize = 12;
        Page<Product> productPage = catalog.list(q, page, pageSize);
        model.addAttribute("products", productPage);
        model.addAttribute("q", q);
        model.addAttribute("categories", catalog.categories());
        return "index";
    }
}