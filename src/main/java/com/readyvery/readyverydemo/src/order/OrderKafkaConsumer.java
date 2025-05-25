package com.readyvery.readyverydemo.src.order;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.readyvery.readyverydemo.domain.Progress;
import com.readyvery.readyverydemo.src.order.dto.OrderRegisterRes;
import com.readyvery.readyverydemo.src.store.dto.OrderUpdateMessage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderKafkaConsumer {

	private final StoreSseEmitterManager storeSseEmitterManager;
	private final OrderService orderServiceImpl;

	@KafkaListener(topics = "order_updates", groupId = "store_owner_group")
	public void processOrderUpdate(OrderUpdateMessage message) {
		log.info("[OrderKafkaConsumer] Received message for storeId: {}, orderId: {}, status: {}",
			message.getStoreId(), message.getOrderId(), message.getStatus());

		OrderRegisterRes orderList = orderServiceImpl.getOrdersV2(message.getStoreId(), Progress.INTEGRATION);
		storeSseEmitterManager.sendEventToStore(message.getStoreId(), orderList);
	}
}
