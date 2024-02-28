package com.readyvery.readyverydemo.src.point;

import com.readyvery.readyverydemo.domain.Order;
import com.readyvery.readyverydemo.domain.Progress;

import jakarta.transaction.Transactional;

public interface PointService {
	void giveOrderPoint(Order order, Progress status);
}
