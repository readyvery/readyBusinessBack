package com.readyvery.readyverydemo.src.inventory.dto;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class InventoryDto {
	private String name;
	private List<InventoryStockDto> foodies;
}
