package com.readyvery.readyverydemo.src.order.dto;

import java.util.List;

import org.springframework.stereotype.Component;

import com.readyvery.readyverydemo.domain.CartItem;
import com.readyvery.readyverydemo.domain.CartOption;
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
			.orderId(order.getOrderId())
			.pickUp(order.getInOut())
			.time(order.getCreatedAt())
			.phone(order.getUserInfo().getPhone())
			.price(order.getAmount())
			.method(order.getMethod())
			.foodies(order.getCart()
				.getCartItems()
				.stream()
				.filter(cartItem -> !cartItem.getIsDeleted())
				.map(this::cartItemToFoodieDto)
				.toList())
			.build();

	}

	private FoodieDto cartItemToFoodieDto(CartItem cartItem) {
		return FoodieDto.builder()
			.name(cartItem.getFoodie().getName())
			.count(cartItem.getCount())
			.options(
				cartItem.getCartOptions().stream().map(this::cartItemToOptionDto).toList())
			.build();
	}

	private OptionDto cartItemToOptionDto(CartOption cartOption) {
		return OptionDto.builder()
			.name(cartOption.getFoodieOption().getName())
			.category(cartOption.getFoodieOption().getFoodieOptionCategory().getName())
			.build();
	}

	public DefaultRes tosspaymentDtoToCancelRes() {
		return DefaultRes.builder()
			.message("취소 성공")
			.build();
	}
}
