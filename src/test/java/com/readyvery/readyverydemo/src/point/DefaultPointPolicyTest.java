package com.readyvery.readyverydemo.src.point;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DefaultPointPolicyTest {
	@InjectMocks
	private DefaultPointPolicy defaultPointPolicy;

	@Test
	void calculatePoint() {
		// given
		Long amount = 10000L; // 계산 금액
		// when
		Long point = defaultPointPolicy.calculatePoint(amount);
		// then
		assertEquals(250L, point);
	}
}