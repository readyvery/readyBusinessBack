package com.readyvery.readyverydemo.src.inventory;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.readyvery.readyverydemo.security.jwt.dto.CustomUserDetails;
import com.readyvery.readyverydemo.src.inventory.dto.InventoryStockRes;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class InventoryController {

	private final InventoryService inventoryServiceImpl;

	@GetMapping("/inventory")
	public InventoryStockRes getInventorys(@AuthenticationPrincipal CustomUserDetails userDetails) {
		return inventoryServiceImpl.getInventorys(userDetails.getId());
	}
}
