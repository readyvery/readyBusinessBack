package com.readyvery.readyverydemo.src.order;

import java.util.List;

import org.springframework.stereotype.Service;

import com.readyvery.readyverydemo.domain.CeoInfo;
import com.readyvery.readyverydemo.domain.Order;
import com.readyvery.readyverydemo.domain.Progress;
import com.readyvery.readyverydemo.domain.repository.CeoRepository;
import com.readyvery.readyverydemo.domain.repository.OrderRepository;
import com.readyvery.readyverydemo.global.exception.BusinessLogicException;
import com.readyvery.readyverydemo.global.exception.ExceptionCode;
import com.readyvery.readyverydemo.src.order.dto.OrderMapper;
import com.readyvery.readyverydemo.src.order.dto.OrderRegisterRes;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

	private final OrderRepository orderRepository;
	private final OrderMapper orderMapper;
	private final CeoRepository ceoRepository;

	@Override
	public OrderRegisterRes getOrder(Long id, Progress progress) {
		CeoInfo ceoInfo = getCeoInfo(id);

		if (progress == null) {
			throw new BusinessLogicException(ExceptionCode.NOT_PROGRESS_ORDER);
		}

		List<Order> orders = orderRepository.findAllByProgressAndStoreId(progress, ceoInfo.getStore().getId());
		log.info("progress: {}", orders);
		if (orders.isEmpty()) {
			throw new BusinessLogicException(ExceptionCode.NOT_FOUND_ORDER);
		}
		return orderMapper.orderToOrderRegisterRes(orders);
	}

	private CeoInfo getCeoInfo(Long id) {
		return ceoRepository.findById(id).orElseThrow(
			() -> new BusinessLogicException(ExceptionCode.USER_NOT_FOUND)
		);
	}

}
