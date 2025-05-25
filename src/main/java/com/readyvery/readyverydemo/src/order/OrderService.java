package com.readyvery.readyverydemo.src.order;

import com.readyvery.readyverydemo.domain.Order;
import com.readyvery.readyverydemo.domain.Progress;
import com.readyvery.readyverydemo.src.order.dto.OrderRegisterRes;
import com.readyvery.readyverydemo.src.order.dto.OrderStatusRes;
import com.readyvery.readyverydemo.src.order.dto.OrderStatusUpdateReq;

public interface OrderService {

	OrderRegisterRes getOrders(Long id, Progress progress);

	OrderRegisterRes getOrdersV2(Long id, Progress progress);

	OrderStatusRes completeOrder(Long id, OrderStatusUpdateReq request);

	OrderStatusRes cancelOrder(Long id, OrderStatusUpdateReq request);

	void cancelTossPayment(Order order, OrderStatusUpdateReq request);
}
