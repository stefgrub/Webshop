package com.example.shop.web.admin;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ProductImageForm {
    private String imageUrl;
    private String caption;
    private Integer sortIndex = 0;

}