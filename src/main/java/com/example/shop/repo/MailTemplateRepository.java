package com.example.shop.repo;

import com.example.shop.domain.MailTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MailTemplateRepository extends JpaRepository<MailTemplate, String> {}