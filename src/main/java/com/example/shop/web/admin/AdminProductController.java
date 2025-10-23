package com.example.shop.web.admin;

import com.example.shop.domain.Product;
import com.example.shop.repo.ProductRepo;
import com.example.shop.repo.CategoryRepo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/products")
public class AdminProductController {

    private final ProductRepo products;
    private final CategoryRepo categories;

    public AdminProductController(ProductRepo products, CategoryRepo categories) {
        this.products = products;
        this.categories = categories;
    }

    @GetMapping
    public String list(@RequestParam(value = "q", required = false, defaultValue = "") String q,
                       @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
                       Model m) {
        Page<Product> page = (q == null || q.isBlank())
                ? products.findAll(pageable)
                : products.findByNameContainingIgnoreCase(q, pageable); // oder Name+Beschreibung (siehe Repo)
        m.addAttribute("page", page);
        m.addAttribute("q", q);
        return "admin_products";
    }

    @GetMapping("/new")
    public String createForm(Model m) {
        m.addAttribute("product", new Product());
        m.addAttribute("categories", categories.findAll());
        return "admin_product_form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model m) {
        Product p = products.findById(id).orElseThrow();
        m.addAttribute("product", p);
        m.addAttribute("categories", categories.findAll());
        return "admin_product_form";
    }

    @PostMapping
    public String create(@ModelAttribute Product product) {
        Long catId = product.getCategory() != null ? product.getCategory().getId() : null;
        product.setCategory(catId != null ? categories.getReferenceById(catId) : null);
        if(product.getStock() == null) product.setStock(0);
        products.save(product);
        return "redirect:/admin/products";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @ModelAttribute Product form) {
        Product p = products.findById(id).orElseThrow();
        p.setName(form.getName());
        p.setSlug(form.getSlug());
        p.setPriceCents(form.getPriceCents());
        p.setDescription(form.getDescription());
        p.setImageUrl(form.getImageUrl());
        p.setStock(form.getStock());

        Long catId = form.getCategory() != null ? form.getCategory().getId() : null;
        p.setCategory(catId != null ? categories.getReferenceById(catId) : null);

        products.save(p);
        return "redirect:/admin/products";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        products.deleteById(id);
        return "redirect:/admin/products";
    }
}
