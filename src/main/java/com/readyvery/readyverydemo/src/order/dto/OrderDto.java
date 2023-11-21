package com.readyvery.readyverydemo.src.order.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class OrderDto {
	private Long idx;
	private String orderNum;
	private String pickUp;
	private List<FoodieDto> foodies;
	private String phone;
	private LocalDateTime time;
	private Long price;
}
