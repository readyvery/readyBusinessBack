package com.readyvery.readyverydemo.src.inventory;

import com.readyvery.readyverydemo.src.inventory.dto.InventoryStockRes;
import com.readyvery.readyverydemo.src.inventory.dto.InventoryUpdateReq;
import com.readyvery.readyverydemo.src.inventory.dto.InventoryUpdateRes;

public interface InventoryService {
	InventoryStockRes getInventorys(Long id);

	InventoryUpdateRes updateInventorys(Long id, InventoryUpdateReq request);
}
