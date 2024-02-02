package com.readyvery.readyverydemo.src.store;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.readyvery.readyverydemo.security.jwt.dto.CustomUserDetails;
import com.readyvery.readyverydemo.src.store.dto.StatusUpdateReq;
import com.readyvery.readyverydemo.src.store.dto.StoreStatusRes;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/store")
public class StoreController {

	private final StoreService storeServiceImpl;

	@Operation(summary = "영업중 여부 확인 기능", description = "영업중 여부를 확인합니다.", tags = {"영업"})
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK"),
	})
	@GetMapping("/sales")
	public StoreStatusRes getStoreSales(@AuthenticationPrincipal CustomUserDetails userDetails) {
		return storeServiceImpl.getStoreStatusById(userDetails.getId());
	}

	@Operation(summary = "영업중 상태 변경 기능", description = "영업중 상태를 변경합니다.", tags = {"영업"})
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK"),
	})
	@PostMapping("/sales")
	public StoreStatusRes updateStoreSales(@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestBody StatusUpdateReq request) {
		return storeServiceImpl.updateStoreStatusById(userDetails.getId(), request.isStatus());
	}

}
