package com.readyvery.readyverydemo.src.order;

import com.readyvery.readyverydemo.domain.Progress;
import com.readyvery.readyverydemo.src.order.dto.OrderRegisterRes;

public interface OrderService {

	OrderRegisterRes getOrder(Long id, Progress progress);
}
