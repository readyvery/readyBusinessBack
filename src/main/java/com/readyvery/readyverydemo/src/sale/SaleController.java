package com.readyvery.readyverydemo.src.sale;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.readyvery.readyverydemo.security.jwt.dto.CustomUserDetails;
import com.readyvery.readyverydemo.src.sale.dto.TotalSaleRes;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class SaleController {

	private final SaleService saleServiceImpl;

	@GetMapping("/sale/total")
	public TotalSaleRes getTotalSales(@AuthenticationPrincipal CustomUserDetails userDetails) {
		return saleServiceImpl.getTotalSaleMoney(userDetails.getId());
	}

}
