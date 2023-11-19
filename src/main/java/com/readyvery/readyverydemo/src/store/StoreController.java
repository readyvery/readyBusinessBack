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

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/store")
public class StoreController {

	private final StoreService storeServiceImpl;

	@GetMapping("/sales")
	public StoreStatusRes getStoreSales(@AuthenticationPrincipal CustomUserDetails userDetails) {
		return storeServiceImpl.getStoreStatusById(userDetails.getId());
	}

	@PostMapping("/sales")
	public StoreStatusRes updateStoreSales(@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestBody StatusUpdateReq request) {
		return storeServiceImpl.updateStoreStatusById(userDetails.getId(), request.isStatus());
	}

}
