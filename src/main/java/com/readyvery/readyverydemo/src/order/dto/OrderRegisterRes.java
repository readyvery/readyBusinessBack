package com.readyvery.readyverydemo.src.order.dto;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrderRegisterRes {
	private Long storeIdx;
	private List<OrderDto> orders;

	private List<OrderDto> integrationMakeOrders;

}
