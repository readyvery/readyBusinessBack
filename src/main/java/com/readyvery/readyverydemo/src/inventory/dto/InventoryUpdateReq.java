package com.readyvery.readyverydemo.src.inventory.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class InventoryUpdateReq {
	private Long idx;
	private boolean soldOut;
}
