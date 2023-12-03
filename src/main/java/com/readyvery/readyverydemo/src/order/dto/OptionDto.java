package com.readyvery.readyverydemo.src.order.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OptionDto {
	private String name;
	private Long price;
	private String category;
}
