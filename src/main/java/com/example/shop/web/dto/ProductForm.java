package com.example.shop.web.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ProductForm {
    private String name;
    private String slug;
    private Integer priceCents;
    private String description;
    private Integer stock;
    private String imageUrl;
    private Long categoryId;

    public ProductForm() {}

}