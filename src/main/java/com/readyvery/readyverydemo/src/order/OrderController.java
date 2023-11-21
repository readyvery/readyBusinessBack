package com.readyvery.readyverydemo.src.order;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.readyvery.readyverydemo.src.order.dto.OrderRegisterRes;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/order")
public class OrderController {

	private final OrderService orderServiceImpl;

	@GetMapping("/")
	public OrderRegisterRes getOrder() {
		return orderServiceImpl.getOrder();
	}
}
