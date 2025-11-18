package com.example.shop.repo;
import com.example.shop.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;
import java.util.List;
import com.example.shop.domain.Category;

public interface ProductRepo extends JpaRepository<Product, Long> {

    Optional<Product> findBySlug(String slug);

    @EntityGraph(attributePaths = "category")
    Page<Product> findAll(Pageable pageable);

    @EntityGraph(attributePaths = "category")
    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);

    @EntityGraph(attributePaths = "category")
    Page<Product> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
            String q1, String q2, Pageable pageable);

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

    @EntityGraph(attributePaths = "category")
    List<Product> findTop5ByCategoryAndIdNotOrderByIdDesc(Category category, Long excludeId);

}