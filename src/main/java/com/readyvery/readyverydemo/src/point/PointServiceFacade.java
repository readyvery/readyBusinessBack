package com.readyvery.readyverydemo.src.point;

import java.util.List;

import org.springframework.stereotype.Service;

import com.readyvery.readyverydemo.domain.Order;
import com.readyvery.readyverydemo.domain.Point;
import com.readyvery.readyverydemo.domain.repository.PointRepository;
import com.readyvery.readyverydemo.global.exception.BusinessLogicException;
import com.readyvery.readyverydemo.global.exception.ExceptionCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PointServiceFacade {
	private final PointRepository pointRepository;

	public void savePoint(Point point) {
		pointRepository.save(point);
	}

	public void cancelPoints(Order order) {
		List<Point> points = getPointsByOrder(order);
		points.forEach(point -> point.setIsDeleted(true));
		pointRepository.saveAll(points);
	}

	public List<Point> getPointsByOrder(Order order) {
		List<Point> points = pointRepository.findAllByOrder(order);
		if (points.isEmpty()){
			throw new BusinessLogicException(ExceptionCode.POINT_NOT_FOUND);
		}
		return points;
	}
}
