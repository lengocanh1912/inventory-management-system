package com.ims.repository;

import com.ims.entity.Order;
import com.ims.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByOrderCode(String orderCode);
    boolean existsByOrderCode(String orderCode);

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.staff " +
           "WHERE (:search IS NULL OR LOWER(o.orderCode) LIKE LOWER(CONCAT('%',:search,'%')) " +
           "OR LOWER(o.customerName) LIKE LOWER(CONCAT('%',:search,'%'))) " +
           "AND (:status IS NULL OR o.status = :status)")
    Page<Order> searchOrders(
            @Param("search") String search,
            @Param("status") OrderStatus status,
            Pageable pageable);

    long countByStatus(OrderStatus status);

    @Query("SELECT COALESCE(SUM(o.total), 0) FROM Order o WHERE o.status = 'COMPLETED'")
    BigDecimal getTotalCompletedRevenue();

    @Query("SELECT COALESCE(SUM(o.total), 0) FROM Order o " +
           "WHERE o.status = 'COMPLETED' AND o.createdAt BETWEEN :start AND :end")
    BigDecimal getRevenueBetween(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.createdAt BETWEEN :start AND :end")
    long countOrdersBetween(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    List<Order> findTop5ByOrderByCreatedAtDesc();
}
