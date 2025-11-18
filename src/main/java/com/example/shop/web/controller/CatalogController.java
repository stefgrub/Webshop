package com.example.shop.web.controller;

import com.example.shop.domain.Product;
import com.example.shop.service.CatalogService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;

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
    @GetMapping("/products/{slug}")
    public String productDetail(@PathVariable String slug, Model model) {
        Product p = catalog.getProduct(slug);
        model.addAttribute("p", p);

        var recommended = catalog.recommendProducts(p, 4);
        model.addAttribute("recommended", recommended);

        return "product";
    }
}