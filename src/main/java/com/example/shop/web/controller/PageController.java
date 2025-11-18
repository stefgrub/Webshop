package com.example.shop.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/impressum-en")
    public String impressumEn() {
        return "impressum_en"; // Template: impressum_en.html
    }

    @GetMapping("/datenschutz-en")
    public String datenschutzEn() {
        return "datenschutz_en"; // Template: datenschutz_en.html
    }
}