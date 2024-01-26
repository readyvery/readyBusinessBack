package com.readyvery.readyverydemo.src.inventory;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.readyvery.readyverydemo.security.jwt.dto.CustomUserDetails;
import com.readyvery.readyverydemo.src.inventory.dto.InventoryStockRes;
import com.readyvery.readyverydemo.src.inventory.dto.InventoryUpdateReq;
import com.readyvery.readyverydemo.src.inventory.dto.InventoryUpdateRes;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class InventoryController {

	private final InventoryService inventoryServiceImpl;

	@Operation(summary = "재고 관리 현황 기능", description = "재고 현황을 가져옵니다.", tags = {"재고 관리"})
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK"),
	})
	@GetMapping("/inventory")
	public InventoryStockRes getInventorys(@AuthenticationPrincipal CustomUserDetails userDetails) {
		return inventoryServiceImpl.getInventorys(userDetails.getId());
	}

	@Operation(summary = "재고 관리 변경 기능", description = "재고 현황 변경합니다.", tags = {"재고 관리"})
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK"),
	})
	@PatchMapping("/inventory")
	public InventoryUpdateRes updateInventorys(@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestBody InventoryUpdateReq request) {
		return inventoryServiceImpl.updateInventorys(userDetails.getId(), request);
	}
}
