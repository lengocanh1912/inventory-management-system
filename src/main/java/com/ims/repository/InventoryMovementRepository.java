package com.ims.repository;

import com.ims.entity.InventoryMovement;
import com.ims.enums.MovementType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryMovementRepository extends JpaRepository<InventoryMovement, Long> {

    @Query("SELECT m FROM InventoryMovement m LEFT JOIN FETCH m.product LEFT JOIN FETCH m.performedBy " +
            "WHERE (:productId IS NULL OR m.product.id = :productId) " +
            "AND (:type IS NULL OR m.type = :type) " +
            "ORDER BY m.createdAt DESC")
    Page<InventoryMovement> findMovements(
            @Param("productId") Long productId,
            @Param("type") MovementType type,
            Pageable pageable);

    // JOIN FETCH để tránh LazyInit khi đọc product và performedBy
    @Query("SELECT m FROM InventoryMovement m LEFT JOIN FETCH m.product LEFT JOIN FETCH m.performedBy ORDER BY m.createdAt DESC")
    List<InventoryMovement> findTop10ByOrderByCreatedAtDesc(Pageable pageable);
}