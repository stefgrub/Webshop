package com.example.shop.web.controller;

import com.example.shop.domain.Category;
import com.example.shop.repo.CategoryRepo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/categories")
public class AdminCategoryController {

    private final CategoryRepo categories;

    public AdminCategoryController(CategoryRepo categories) {
        this.categories = categories;
    }

    @GetMapping
    public String list(Model m) {
        m.addAttribute("categories", categories.findAll());
        return "admin_categories"; // Liste
    }

    @GetMapping("/new")
    public String createForm(Model m) {
        m.addAttribute("category", new Category());
        return "admin_category_form"; // Formular
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model m) {
        Category c = categories.findById(id).orElseThrow();
        m.addAttribute("category", c);
        return "admin_category_form";
    }

    @PostMapping
    public String create(@ModelAttribute("category") Category category) {
        categories.save(category);
        return "redirect:/admin/categories";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @ModelAttribute("category") Category form) {
        Category c = categories.findById(id).orElseThrow();
        c.setName(form.getName());
        c.setSlug(form.getSlug());
        categories.save(c);
        return "redirect:/admin/categories";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        categories.deleteById(id);
        return "redirect:/admin/categories";
    }
}