package com.example.shop.web.admin;

import com.example.shop.domain.MailTemplate;
import com.example.shop.service.MailTemplateService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/mail-templates")
public class AdminMailTemplateController {

    private final MailTemplateService service;

    public AdminMailTemplateController(MailTemplateService service) {
        this.service = service;
    }

    @GetMapping
    public String list(Model model) {
        List<MailTemplate> all = service.findAll();
        model.addAttribute("templates", all);
        return "admin/mail_templates";
    }

    @GetMapping("/{code}")
    public String edit(@PathVariable String code, Model model) {
        MailTemplate t = service.findByCode(code).orElseGet(() -> {
            MailTemplate mt = new MailTemplate();
            mt.setCode(code);
            mt.setSubject("");
            mt.setBodyHtml("");
            return mt;
        });
        model.addAttribute("template", t);
        return "admin/mail_template_edit";
    }

    @PostMapping
    public String save(@RequestParam String code,
                       @RequestParam String subject,
                       @RequestParam String bodyHtml) {
        service.save(code, subject, bodyHtml);
        return "redirect:/admin/mail-templates";
    }
}