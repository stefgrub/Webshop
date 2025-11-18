package com.example.shop.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "products")
public class Product {

    // --- getters/setters ---
    @Setter
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Getter
    @NotBlank private String name;

    @Setter
    @Getter
    @NotBlank private String slug;

    @Min(0) private int priceCents;

    @Setter
    @Getter
    @Size(max=2000) private String description;

    @Setter
    @Getter
    @Column(nullable = false)
    private Integer stock;

    @Setter
    @Getter
    @Column(name = "image_url")
    private String imageUrl;

    @Transient
    public String getImagePath() {
        String resolved = getImageUrlResolved();
        if (resolved != null) {
            return resolved;
        }
        return "/img/placeholder.png";
    }

    @Setter
    @Getter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    public Product() {}

    public Integer getPriceCents() { return priceCents; }
    public void setPriceCents(Integer priceCents) { this.priceCents = priceCents; }

    @Transient
    public String getImageUrlResolved() {
        if (imageUrl == null || imageUrl.isBlank()) {
            return null;
        }

        String trimmed = imageUrl.trim();

        // Externe URLs 1:1 übernehmen
        if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) {
            return trimmed;
        }

        // Bereits vollständige Pfade (z.B. /media/..., /images/...)
        if (trimmed.startsWith("/")) {
            return trimmed;
        }

        // Nur Dateiname -> wir gehen davon aus, dass es aus /media kommt
        return "/media/" + trimmed;
    }

    @Setter
    @Getter
    @OneToMany(mappedBy = "product",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE},
            orphanRemoval = true)
    @OrderBy("sortIndex ASC, id ASC")
    private java.util.List<ProductImage> images = new java.util.ArrayList<>();

    @Setter
    @Getter
    @OneToMany(mappedBy = "product", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private java.util.List<OrderItem> orderItems = new java.util.ArrayList<>();

}