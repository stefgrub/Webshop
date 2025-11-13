package com.example.shop.service;

import com.example.shop.domain.FeatureFlag;
import com.example.shop.repo.FeatureFlagRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FeatureFlagService {
    private final FeatureFlagRepository repo;

    public FeatureFlagService(FeatureFlagRepository repo) {
        this.repo = repo;
    }

    public List<FeatureFlag> getAll() {
        return repo.findAll();
    }

    // ggf. noch Hilfsfunktionen wie isEnabled("name")
}