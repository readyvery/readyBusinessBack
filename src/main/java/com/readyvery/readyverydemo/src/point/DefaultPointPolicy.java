package com.readyvery.readyverydemo.src.point;

public class DefaultPointPolicy implements PointPolicy{
	private Double pointRate = 2.5;

	public Long calculatePoint(Double price) {
		Double tmp = price * pointRate + 0.5; // 반올림
		return tmp.longValue();
	}
}
