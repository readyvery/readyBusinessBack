package com.readyvery.readyverydemo.domain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.readyvery.readyverydemo.domain.Order;
import com.readyvery.readyverydemo.domain.Progress;

public interface OrderRepository extends JpaRepository<Order, Long> {
	List<Order> findAllById(Long id);

	List<Order> findAllByProgressAndStoreId(Progress progress, Long storeId);

	Optional<Order> findByOrderId(String orderId);
}
