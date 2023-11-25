package com.readyvery.readyverydemo.src.inventory;

import com.readyvery.readyverydemo.src.inventory.dto.InventoryStockRes;

public interface InventoryService {
	InventoryStockRes getInventorys(Long id);
}
