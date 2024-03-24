package com.readyvery.readyverydemo.src.point;

import org.springframework.stereotype.Service;

import com.readyvery.readyverydemo.domain.Order;
import com.readyvery.readyverydemo.domain.Point;
import com.readyvery.readyverydemo.domain.repository.PointRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PointServiceFacade {
	private final PointRepository pointRepository;

	public void savePoint(Point point) {
		pointRepository.save(point);
	}

	public void cancelPoint(Order order) {
		Point point = getPointByOrder(order);
		point.setIsDeleted(true);
		pointRepository.save(point);
	}

	public Point getPointByOrder(Order order) {
		return pointRepository.findByOrder(order).orElseThrow(
			() -> new RuntimeException("Point not found")
		);
	}
}
