package com.example.shop.web.admin;

import com.example.shop.domain.Product;
import com.example.shop.domain.ProductImage;
import com.example.shop.repo.ProductImageRepo;
import com.example.shop.repo.ProductRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/products/{productId}/images")
public class AdminProductImageController {

    private final ProductRepo productRepo;
    private final ProductImageRepo imageRepo;

    private Product getProductOrThrow(Long productId) {
        return productRepo.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Produkt nicht gefunden: " + productId));
    }

    @GetMapping
    public String list(@PathVariable Long productId, Model model) {
        Product p = getProductOrThrow(productId);
        List<ProductImage> images = imageRepo.findByProductOrderBySortIndexAscIdAsc(p);

        model.addAttribute("product", p);
        model.addAttribute("images", images);
        model.addAttribute("newImage", new ProductImageForm());
        return "admin/product_images";
    }

    @PostMapping
    public String add(@PathVariable Long productId,
                      @ModelAttribute("newImage") ProductImageForm form,
                      RedirectAttributes ra) {

        Product p = getProductOrThrow(productId);

        if (form.getImageUrl() == null || form.getImageUrl().isBlank()) {
            ra.addFlashAttribute("error", "Bild-URL darf nicht leer sein.");
            return "redirect:/admin/products/" + productId + "/images";
        }

        ProductImage img = new ProductImage();
        img.setProduct(p);
        img.setImageUrl(form.getImageUrl().trim());
        img.setCaption(form.getCaption());
        img.setSortIndex(form.getSortIndex() != null ? form.getSortIndex() : 0);

        imageRepo.save(img);

        ra.addFlashAttribute("success", "Bild zur Galerie hinzugefügt.");
        return "redirect:/admin/products/" + productId + "/images";
    }

    @PostMapping("/{imageId}/delete")
    public String delete(@PathVariable Long productId,
                         @PathVariable Long imageId,
                         RedirectAttributes ra) {

        imageRepo.deleteById(imageId);
        ra.addFlashAttribute("success", "Bild wurde gelöscht.");
        return "redirect:/admin/products/" + productId + "/images";
    }
}