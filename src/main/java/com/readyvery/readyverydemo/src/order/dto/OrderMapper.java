package com.readyvery.readyverydemo.src.order.dto;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;

import com.readyvery.readyverydemo.domain.CartItem;
import com.readyvery.readyverydemo.domain.CartOption;
import com.readyvery.readyverydemo.domain.Order;

@Component
public class OrderMapper {

	public OrderRegisterRes orderToOrderRegisterRes(Long storeIdx, List<Order> order,
		List<Order> integrationMakeOrder) {

		List<OrderDto> integratedMakeOrderDtos = integrationMakeOrder.isEmpty() ? Collections.emptyList() :
			integrationMakeOrder.stream().map(this::toOrderDto).toList();

		List<OrderDto> orderDtos = order.isEmpty() ? Collections.emptyList() :
			order.stream().map(this::toOrderDto).toList();

		return OrderRegisterRes.builder()
			.storeIdx(storeIdx)
			.integrationMakeOrders(integratedMakeOrderDtos)
			.orders(orderDtos)
			.build();
	}

	private OrderDto toOrderDto(Order order) {
		return OrderDto.builder()
			.idx(order.getId())
			.orderNum(order.getOrderNumber())
			.orderId(order.getOrderId())
			.progress(order.getProgress())
			.pickUp(order.getInOut())
			.time(order.getCreatedAt())
			.phone(order.getUserInfo().getPhone())
			.price(order.getTotalAmount())
			.salePrice(order.getAmount())
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
			.price(cartOption.getFoodieOption().getPrice())
			.category(cartOption.getFoodieOption().getFoodieOptionCategory().getName())
			.build();
	}

	public DefaultRes tosspaymentDtoToCancelRes() {
		return DefaultRes.builder()
			.message("취소 성공")
			.build();
	}
}
