package com.readyvery.readyverydemo.src.point;

import org.springframework.stereotype.Service;

import com.readyvery.readyverydemo.domain.Order;
import com.readyvery.readyverydemo.domain.Point;
import com.readyvery.readyverydemo.domain.Progress;
import com.readyvery.readyverydemo.domain.UserInfo;
import com.readyvery.readyverydemo.src.user.UserServiceFacade;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PointServiceImpl implements PointService {
	private final PointPolicy pointPolicy;
	private final UserServiceFacade userServiceFacade;
	private final PointServiceFacade pointServiceFacade;

	@Override
	public void giveOrderPoint(Order order, Progress status) {
		if (status.equals(Progress.COMPLETE)) {
			UserInfo userInfo = userServiceFacade.getUserInfo(order.getUserInfo().getId());
			userInfo.addPoint(pointPolicy.calculatePoint(order.getAmount()));
			pointServiceFacade.savePoint(Point.builder()
				.userInfo(userInfo)
				.order(order)
				.point(pointPolicy.calculatePoint(order.getAmount()))
				.isDeleted(false)
				.build());
			userServiceFacade.saveUser(userInfo);
		}
	}

	@Override
	public void cancelOrderPoint(Order order) {
		UserInfo userInfo = userServiceFacade.getUserInfo(order.getUserInfo().getId());
		userInfo.addPoint(-order.getPoint());
		userInfo.addPoint(-pointPolicy.calculatePoint(order.getAmount()));
		userServiceFacade.saveUser(userInfo);
	}
}
