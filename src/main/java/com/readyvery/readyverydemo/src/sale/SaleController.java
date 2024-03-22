package com.readyvery.readyverydemo.src.sale;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.readyvery.readyverydemo.security.jwt.dto.CustomUserDetails;
import com.readyvery.readyverydemo.src.sale.dto.SaleManagementReq;
import com.readyvery.readyverydemo.src.sale.dto.SaleManagementRes;
import com.readyvery.readyverydemo.src.sale.dto.SaleManagementTotalMoneyReq;
import com.readyvery.readyverydemo.src.sale.dto.SaleManagementTotalMoneyRes;
import com.readyvery.readyverydemo.src.sale.dto.SaleManagementTotalOrderReq;
import com.readyvery.readyverydemo.src.sale.dto.SaleManagementTotalOrderRes;
import com.readyvery.readyverydemo.src.sale.dto.TotalSaleRes;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
public class SaleController {

	private final SaleService saleServiceImpl;

	@Operation(summary = "매출 총 금액 확인 기능", description = "매출 총 금액을 확인합니다.", tags = {"매출"})
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK"),
	})
	@GetMapping("/sale/total")
	public TotalSaleRes getTotalSales(@AuthenticationPrincipal CustomUserDetails userDetails) {
		return saleServiceImpl.getTotalSaleMoney(userDetails.getId());
	}

	@Operation(summary = "매출 관리 기능", description = "매출을 관리합니다.", tags = {"매출"})
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK"),
	})
	@PostMapping("/sale/management")
	public SaleManagementRes getSaleManagement(@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestBody SaleManagementReq request) {
		return saleServiceImpl.getSaleManagementMoney(userDetails.getId(), request);
	}

	@Operation(summary = "월간, 주간 총 주문 건수 기능", description = "월간, 주간 총 주문 건수를 출력합니다.", tags = {"매출"})
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK"),
	})
	@PostMapping("/sale/management/order/total")
	public SaleManagementTotalOrderRes getSaleManagementOrder(@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestBody SaleManagementTotalOrderReq saleManagementTotalOrderReq) {
		return saleServiceImpl.getSaleManagementOrder(userDetails.getId(), saleManagementTotalOrderReq);
	}

	@Operation(summary = "월간 총 매출 기능", description = "월간 총 매출을 출력합니다.", tags = {"매출"})
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK"),
	})
	@PostMapping("/sale/management/amount/monthly")
	public SaleManagementTotalMoneyRes getMonthlySalesAmount(@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestBody SaleManagementTotalMoneyReq saleManagementTotalMoneyReq) {
		return saleServiceImpl.getMonthlySalesAmount(userDetails.getId(), saleManagementTotalMoneyReq);
	}

	@Operation(summary = "주간 총 매출 기능", description = "월 총 매출을 출력합니다.", tags = {"매출"})
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK"),
	})
	@PostMapping("/sale/management/amount/weekly")
	public SaleManagementTotalMoneyRes getWeekSaleManagementMoney(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestBody SaleManagementTotalMoneyReq saleManagementTotalMoneyReq) {
		return saleServiceImpl.getWeekSaleManagementMoney(userDetails.getId(), saleManagementTotalMoneyReq);
	}

}
