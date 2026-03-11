package com.ims.repository;

import com.ims.entity.Product;
import com.ims.enums.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findBySku(String sku);
    boolean existsBySku(String sku);

    // JOIN FETCH category để tránh LazyInitializationException
    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.category " +
            "WHERE (:search IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%',:search,'%')) " +
            "OR LOWER(p.sku) LIKE LOWER(CONCAT('%',:search,'%'))) " +
            "AND (:categoryId IS NULL OR p.category.id = :categoryId) " +
            "AND (:status IS NULL OR p.status = :status)")
    Page<Product> searchProducts(
            @Param("search") String search,
            @Param("categoryId") Long categoryId,
            @Param("status") ProductStatus status,
            Pageable pageable);

    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.category " +
            "WHERE p.stock <= p.minStock AND p.status = 'ACTIVE'")
    List<Product> findLowStockProducts();

    long countByStatus(ProductStatus status);

    // JOIN FETCH để load category cùng lúc, tránh lazy load sau khi session đóng
    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.category ORDER BY p.price DESC")
    List<Product> findTopByOrderByPriceDesc(Pageable pageable);
}