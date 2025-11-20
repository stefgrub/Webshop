package com.example.shop.repo;

import com.example.shop.domain.Product;
import com.example.shop.domain.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.List;

public interface ProductRepo extends JpaRepository<Product, Long> {

    // --- Einzelprodukt ---
    Optional<Product> findBySlug(String slug);

    @EntityGraph(attributePaths = "category")
    Optional<Product> findBySlugAndActiveTrue(String slug);

    // --- Paging / Listen ---

    @EntityGraph(attributePaths = "category")
    Page<Product> findAll(Pageable pageable);            // Admin: alle, auch inaktive

    @EntityGraph(attributePaths = "category")
    Page<Product> findByActiveTrue(Pageable pageable);   // Shop: nur aktive

    // --- Suche nach Name ---
    @EntityGraph(attributePaths = "category")
    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable); // Admin

    @EntityGraph(attributePaths = "category")
    Page<Product> findByNameContainingIgnoreCaseAndActiveTrue(String name, Pageable pageable); // Shop

    // --- Freitextsuche (Name, Description, Slug, Kategorie) ---

    // Variante für Admin (sieht auch inaktive)
    @EntityGraph(attributePaths = "category")
    @Query("""
        select p from Product p
        left join p.category c
        where lower(p.name) like lower(concat('%', :q, '%'))
           or lower(p.description) like lower(concat('%', :q, '%'))
           or lower(p.slug) like lower(concat('%', :q, '%'))
           or lower(c.name) like lower(concat('%', :q, '%'))
        """)
    Page<Product> search(@Param("q") String q, Pageable pageable);

    // Variante für Shop (nur aktive)
    @EntityGraph(attributePaths = "category")
    @Query("""
        select p from Product p
        left join p.category c
        where p.active = true
          and (
              lower(p.name) like lower(concat('%', :q, '%'))
           or lower(p.description) like lower(concat('%', :q, '%'))
           or lower(p.slug) like lower(concat('%', :q, '%'))
           or lower(c.name) like lower(concat('%', :q, '%'))
          )
        """)
    Page<Product> searchActive(@Param("q") String q, Pageable pageable);


    // --- Empfehlungen / ähnliche Produkte ---

    @EntityGraph(attributePaths = "category")
    List<Product> findTop5ByCategoryAndIdNotOrderByIdDesc(Category category, Long excludeId);

    @EntityGraph(attributePaths = "category")
    List<Product> findTop5ByCategoryAndActiveTrueAndIdNotOrderByIdDesc(Category category, Long excludeId);
}