package com.readyvery.readyverydemo.src.point;

import org.springframework.stereotype.Service;

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
}
