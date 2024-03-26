package com.readyvery.readyverydemo.domain.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.readyvery.readyverydemo.domain.Order;
import com.readyvery.readyverydemo.domain.Progress;
import com.readyvery.readyverydemo.src.sale.dto.SaleManagementDto;

import jakarta.persistence.LockModeType;

public interface OrderRepository extends JpaRepository<Order, Long> {
	List<Order> findAllById(Long id);

	List<Order> findAllByProgressAndStoreIdAndCreatedAtBetween(
		Progress progress, Long storeId, LocalDateTime startDateTime, LocalDateTime endDateTime);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
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

	@Query(
		"SELECT SUM(o.totalAmount) FROM Order o WHERE o.store.id = :storeId AND o.estimatedTime IS NOT NULL "
			+ "AND o.createdAt BETWEEN :startOfMonth "
			+ "AND :endOfMonth")
	Optional<Long> sumTotalAmountByStoreIdForMonth(@Param("storeId") Long storeId,
		@Param("startOfMonth") LocalDateTime startOfMonth, @Param("endOfMonth") LocalDateTime endOfMonth);

	@Query(
		"SELECT SUM(o.totalAmount) FROM Order o WHERE o.store.id = :storeId AND o.estimatedTime IS NOT NULL "
			+ "AND o.createdAt BETWEEN :startOfWeek "
			+ "AND :endOfWeek")
	Optional<Long> sumTotalAmountByStoreIdForWeek(@Param("storeId") Long storeId,
		@Param("startOfWeek") LocalDateTime startOfWeek, @Param("endOfWeek") LocalDateTime endOfWeek);

	@Query(
		"SELECT COUNT(o) FROM Order o WHERE o.store.id = :storeId AND o.estimatedTime IS NOT NULL "
			+ "AND o.createdAt BETWEEN :startOfMonth "
			+ "AND :endOfMonth")
	Long countOrdersByStoreIdForMonth(@Param("storeId") Long storeId,
		@Param("startOfMonth") LocalDateTime startOfMonth, @Param("endOfMonth") LocalDateTime endOfMonth);

	@Query(
		"SELECT COUNT(o) FROM Order o WHERE o.store.id = :storeId AND o.estimatedTime IS NOT NULL "
			+ "AND o.createdAt BETWEEN :startOfWeek "
			+ "AND :endOfWeek")
	Long countOrdersByStoreIdForWeek(@Param("storeId") Long storeId, @Param("startOfWeek") LocalDateTime startOfWeek,
		@Param("endOfWeek") LocalDateTime endOfWeek);

}
