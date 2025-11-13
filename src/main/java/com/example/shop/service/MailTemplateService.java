package com.example.shop.service;

import com.example.shop.domain.MailTemplate;
import com.example.shop.repo.MailTemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MailTemplateService {

    private final MailTemplateRepository repo;

    // Wird vom AdminMailTemplateController erwartet
    @Transactional(readOnly = true)
    public List<MailTemplate> findAll() {
        return repo.findAll();
    }

    // Wird vom AdminMailTemplateController erwartet
    @Transactional(readOnly = true)
    public Optional<MailTemplate> findByCode(String code) {
        return repo.findById(code);
    }

    // Wird vom AdminMailTemplateController erwartet
    @Transactional
    public MailTemplate save(String code, String subject, String bodyHtml) {
        MailTemplate mt = repo.findById(code).orElseGet(() -> {
            MailTemplate x = new MailTemplate();
            x.setCode(code);
            return x;
        });
        mt.setSubject(subject);
        mt.setBodyHtml(bodyHtml);
        // Wichtig: OffsetDateTime statt Instant, passt zur Entity
        mt.setUpdatedAt(OffsetDateTime.now(ZoneOffset.UTC));
        return repo.save(mt);
    }

    // Optional nützlich (falls irgendwo verwendet):
    @Transactional
    public void delete(String code) {
        repo.deleteById(code);
    }
}