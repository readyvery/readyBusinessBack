package com.readyvery.readyverydemo.domain.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.readyvery.readyverydemo.domain.Order;
import com.readyvery.readyverydemo.domain.Progress;
import com.readyvery.readyverydemo.src.sale.dto.SaleManagementDto;

public interface OrderRepository extends JpaRepository<Order, Long> {
	List<Order> findAllById(Long id);

	List<Order> findAllByProgressAndStoreId(Progress progress, Long storeId);

	Optional<Order> findByOrderId(String orderId);

	@Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.store.id = :storeId AND o.estimatedTime IS NOT NULL")
	Optional<Long> sumTotalAmountByStoreIdAndEstimatedTime(@Param("storeId") Long storeId);

	@Query("SELECT new com.readyvery.readyverydemo.src.sale.dto.SaleManagementDto"
		+ "(FUNCTION('DAYNAME', FUNCTION('DATE', o.estimatedTime)), SUM(o.totalAmount)) "
		+ "FROM Order o "
		+ "WHERE o.store.id = :storeId AND o.estimatedTime BETWEEN :startDate AND :endDate "
		+ "GROUP BY FUNCTION('DAYNAME', FUNCTION('DATE', o.estimatedTime))")
	List<SaleManagementDto> sumTotalAmountPerDayBetweenDates(@Param("storeId") Long storeId,
		@Param("startDate") LocalDateTime startDate,
		@Param("endDate") LocalDateTime endDate);

}
