package com.readyvery.readyverydemo.src.order.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class OrderRegisterRes {
	private Long storeIdx;
	private List<OrderDto> orders;

	private List<OrderDto> integrationMakeOrders;

}
