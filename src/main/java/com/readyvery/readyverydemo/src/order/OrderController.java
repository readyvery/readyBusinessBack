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

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class OrderController {

	private final OrderService orderServiceImpl;

	@GetMapping("/order")
	public OrderRegisterRes getOrder(@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestParam(required = false) Progress status) {
		return orderServiceImpl.getOrders(userDetails.getId(), status);
	}

	@PostMapping("/order/complete")
	public OrderStatusRes completeOrder(@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestBody OrderStatusUpdateReq request) {
		return orderServiceImpl.completeOrder(userDetails.getId(), request);
	}

	@PostMapping("/order/cancel")
	public OrderStatusRes cancelOrder(@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestBody OrderStatusUpdateReq request) {
		return orderServiceImpl.cancelOrder(userDetails.getId(), request);
	}
}
