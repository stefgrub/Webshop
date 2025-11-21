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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    public String list(
            @RequestParam(value = "q", required = false, defaultValue = "") String q,
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
            Model m) {

        Page<Product> page = (q == null || q.isBlank())
                ? products.findAll(pageable)  // Admin sieht alle, auch inaktive
                : products.findByNameContainingIgnoreCase(q, pageable);

        m.addAttribute("page", page);
        m.addAttribute("q", q);
        return "admin_products";
    }

    @GetMapping("/new")
    public String createForm(Model m) {
        Product p = new Product();
        // Standardmäßig aktiv setzen
        p.setActive(true);

        m.addAttribute("product", p);
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

        if (product.getStock() == null) {
            product.setStock(0);
        }
        if (product.getActive() == null) {
            product.setActive(true);
        }

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
        p.setShortDescription(form.getShortDescription());
        p.setFeatures(form.getFeatures());
        p.setDetails(form.getDetails());
        p.setImageUrl(form.getImageUrl());
        p.setStock(form.getStock());

        // aktiv/inaktiv übernehmen (Default true, falls null)
        p.setActive(form.getActive() != null ? form.getActive() : Boolean.TRUE);

        Long catId = form.getCategory() != null ? form.getCategory().getId() : null;
        p.setCategory(catId != null ? categories.getReferenceById(catId) : null);

        products.save(p);
        return "redirect:/admin/products";
    }

    /**
     * „Löschen“: Produkt nur deaktivieren, damit Bestellungen intakt bleiben.
     * URL: POST /admin/products/{id}/delete
     */
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        Product p = products.findById(id).orElse(null);

        if (p == null) {
            ra.addFlashAttribute("toastError", "Produkt nicht gefunden.");
            return "redirect:/admin/products";
        }

        if (Boolean.FALSE.equals(p.getActive())) {
            ra.addFlashAttribute("toastInfo", "Produkt ist bereits deaktiviert.");
            return "redirect:/admin/products";
        }

        p.setActive(false);
        products.save(p);

        ra.addFlashAttribute("toastSuccess",
                "Produkt wurde deaktiviert und ist im Shop nicht mehr sichtbar (Bestellungen bleiben erhalten).");
        return "redirect:/admin/products";
    }
}