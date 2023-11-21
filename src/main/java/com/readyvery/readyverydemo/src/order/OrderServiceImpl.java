package com.readyvery.readyverydemo.src.order;

import java.util.List;

import org.springframework.stereotype.Service;

import com.readyvery.readyverydemo.domain.Order;
import com.readyvery.readyverydemo.domain.repository.OrderRepository;
import com.readyvery.readyverydemo.global.exception.BusinessLogicException;
import com.readyvery.readyverydemo.global.exception.ExceptionCode;
import com.readyvery.readyverydemo.src.order.dto.OrderMapper;
import com.readyvery.readyverydemo.src.order.dto.OrderRegisterRes;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

	private final OrderRepository orderRepository;
	private final OrderMapper orderMapper;

	@Override
	public OrderRegisterRes getOrder() {
		List<Order> orders = orderRepository.findAll();
		if (orders.isEmpty()) {
			throw new BusinessLogicException(ExceptionCode.NOT_FOUND_ORDER);
		}
		return orderMapper.toOrderRegisterRes(orders);
	}

}
