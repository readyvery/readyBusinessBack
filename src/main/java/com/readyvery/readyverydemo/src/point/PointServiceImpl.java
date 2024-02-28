package com.readyvery.readyverydemo.src.point;

import org.springframework.stereotype.Service;

import com.readyvery.readyverydemo.domain.Order;
import com.readyvery.readyverydemo.domain.Progress;
import com.readyvery.readyverydemo.domain.UserInfo;
import com.readyvery.readyverydemo.src.user.UserServiceFacade;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PointServiceImpl implements PointService{
	private final PointPolicy pointPolicy;
	private final UserServiceFacade userServiceFacade;

	@Override
	public void giveOrderPoint(Order order, Progress status) {
		if (status.equals(Progress.COMPLETE)) {
			UserInfo userInfo = userServiceFacade.getUserInfo(order.getUserInfo().getId());
			userInfo.addPoint(pointPolicy.calculatePoint(order.getAmount()));
			userServiceFacade.saveUser(userInfo);
		}
	}
}
