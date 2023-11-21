package com.readyvery.readyverydemo.src.order.dto;

import java.util.List;

import org.springframework.stereotype.Component;

import com.readyvery.readyverydemo.domain.Order;

@Component
public class OrderMapper {

	public OrderRegisterRes orderToOrderRegisterRes(List<Order> order) {
		if (order.isEmpty()) {
			return OrderRegisterRes.builder()
				.build();
		}
		return OrderRegisterRes.builder()
			.orders(order.stream().map(this::toOrderDto).toList())
			.build();
	}

	private OrderDto toOrderDto(Order order) {
		return OrderDto.builder()
			.idx(order.getId())
			.orderNum(order.getOrderNumber())
			.pickUp(order.getPickUp())
			.name(order.getOrderName())
			.address(order.getAddress())
			.phone(order.getPhone())
			.build();

	}
}
