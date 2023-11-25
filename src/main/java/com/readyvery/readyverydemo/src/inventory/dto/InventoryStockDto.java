package com.readyvery.readyverydemo.src.inventory.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class InventoryStockDto {

	private Long idx;
	private String name;
	private boolean isSoldOut;
}
