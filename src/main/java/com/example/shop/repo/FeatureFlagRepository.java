package com.example.shop.repo;

import com.example.shop.domain.FeatureFlag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeatureFlagRepository extends JpaRepository<FeatureFlag, String> {}