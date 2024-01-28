package com.readyvery.readyverydemo.src.order;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.readyvery.readyverydemo.domain.Progress;
import com.readyvery.readyverydemo.security.jwt.dto.CustomUserDetails;
import com.readyvery.readyverydemo.src.order.dto.OrderRegisterRes;
import com.readyvery.readyverydemo.src.order.dto.OrderStatusRes;
import com.readyvery.readyverydemo.src.order.dto.OrderStatusUpdateReq;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
public class OrderController {

	private final OrderService orderServiceImpl;

	@Operation(summary = "주문 현황 기능", description = "주문 현황을 가져옵니다.", tags = {"주문"})
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK"),
	})
	@GetMapping("/order")
	public OrderRegisterRes getOrder(@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestParam(required = false) Progress status) {
		return orderServiceImpl.getOrders(userDetails.getId(), status);
	}

	@Operation(summary = "주문 상태 변경 기능", description = "주문 상태를 변경합니다.", tags = {"주문"})
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK"),
	})
	@PostMapping("/order/complete")
	public OrderStatusRes completeOrder(@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestBody OrderStatusUpdateReq request) {
		return orderServiceImpl.completeOrder(userDetails.getId(), request);
	}

	@Operation(summary = "주문 취소 기능", description = "사장님 측에서 주문을 취소합니다.", tags = {"주문"})
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK"),
	})
	@PostMapping("/order/cancel")
	public OrderStatusRes cancelOrder(@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestBody OrderStatusUpdateReq request) {
		return orderServiceImpl.cancelOrder(userDetails.getId(), request);
	}
}
