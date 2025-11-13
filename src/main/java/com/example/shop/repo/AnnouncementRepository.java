package com.example.shop.repo;

import com.example.shop.domain.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {

    // bereits verwendet in anderen Stellen:
    List<Announcement> findByActiveTrueOrderByLevelDescUpdatedAtDesc();

    // wird in GlobalAnnouncementAdvice referenziert:
    List<Announcement> findAllByActiveTrueOrderByLevelDescUpdatedAtDesc();
}