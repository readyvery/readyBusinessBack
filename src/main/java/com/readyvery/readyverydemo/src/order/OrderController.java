package com.readyvery.readyverydemo.src.order;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.readyvery.readyverydemo.domain.Progress;
import com.readyvery.readyverydemo.security.jwt.dto.CustomUserDetails;
import com.readyvery.readyverydemo.src.order.dto.OrderRegisterRes;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class OrderController {

	private final OrderService orderServiceImpl;

	@GetMapping("/order")
	public OrderRegisterRes getOrder(@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestParam(required = false) Progress status) {
		return orderServiceImpl.getOrder(userDetails.getId(), status);
	}
}
