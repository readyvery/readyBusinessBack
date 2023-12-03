package com.readyvery.readyverydemo.src.inventory.dto;

import java.util.List;

import org.springframework.stereotype.Component;

import com.readyvery.readyverydemo.domain.Foodie;
import com.readyvery.readyverydemo.domain.FoodieCategory;

@Component
public class InventoryMapper {

	public InventoryStockRes foodiesToInventoryStockRes(List<FoodieCategory> foodieCategories) {
		if (foodieCategories.isEmpty()) {
			return InventoryStockRes.builder()
				.categorys(null)
				.build();
		}

		return InventoryStockRes.builder()
			.categorys(foodieCategories.stream().map(this::toInventoryDto).toList())
			.build();
	}

	private InventoryDto toInventoryDto(FoodieCategory foodieCategories) {
		return InventoryDto.builder()
			.name(foodieCategories.getName())
			.foodies(foodieCategories.getFoodies().stream().map(this::toInventoryStockDto).toList())
			.build();
	}

	private InventoryStockDto toInventoryStockDto(Foodie foodie) {
		return InventoryStockDto.builder()
			.idx(foodie.getId())
			.name(foodie.getName())
			.isSoldOut(foodie.isSoldOut())
			.build();
	}
}
