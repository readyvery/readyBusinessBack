package com.readyvery.readyverydemo.src.inventory.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class InventoryUpdateRes {
	private boolean success;
	private String message;
}
