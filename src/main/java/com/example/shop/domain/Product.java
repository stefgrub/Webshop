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

    @Setter @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter @Getter
    @NotBlank
    @Size(max = 200)
    @Column(nullable = false)
    private String name;

    @Setter @Getter
    @NotBlank
    @Size(max = 200)
    @Column(nullable = false, unique = true)
    private String slug;

    @Setter @Getter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Setter @Getter
    @Min(0)
    @Column(name = "price_cents", nullable = false)
    private Integer priceCents;

    @Setter @Getter
    @Size(max = 500)
    private String shortDescription;

    @Setter @Getter
    @Size(max = 2000)
    private String description;

    @Setter @Getter
    @Column(name = "features")
    private String features;

    @Setter @Getter
    @Column(name = "details")
    private String details;

    @Setter @Getter
    @Column(nullable = false)
    private Integer stock;

    @Setter @Getter
    @Column(nullable = false)
    private Boolean active = true;

    @Setter @Getter
    @Column(name = "image_url")
    private String imageUrl;

    @Transient
    public String getImageUrlResolved() {
        if (imageUrl == null || imageUrl.isBlank()) {
            return null;
        }
        String trimmed = imageUrl.trim();
        if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) return trimmed;
        if (trimmed.startsWith("/")) return trimmed;
        return "/media/" + trimmed;
    }

    @Transient
    public String getImagePath() {
        String resolved = getImageUrlResolved();
        return resolved != null ? resolved : "/img/placeholder.png";
    }
}