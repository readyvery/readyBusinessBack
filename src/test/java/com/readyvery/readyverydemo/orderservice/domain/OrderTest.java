package com.readyvery.readyverydemo.orderservice.domain;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.readyvery.readyverydemo.domain.Cart;
import com.readyvery.readyverydemo.domain.Coupon;
import com.readyvery.readyverydemo.domain.Order;
import com.readyvery.readyverydemo.domain.Progress;
import com.readyvery.readyverydemo.domain.Store;
import com.readyvery.readyverydemo.domain.UserInfo;

class OrderTest {

	@DisplayName("총 금액은 null이 아니어야 한다.")
	@Test
	void validateTotalAmount() {
		Store store = Store.builder()
			.name("testStore")
			.address("testAddress")
			.phone("testPhone")
			.time("09:00-21:00")
			.status(true)
			.build();

		UserInfo userInfo = UserInfo.builder()
			.nickName("testUser")
			.email("test@example.com")
			.build();

		Cart cart = Cart.builder().build();
		Coupon coupon = Coupon.builder().build();

		Order order = Order.builder()
			.amount(500L)
			.paymentKey("testPaymentKey")
			.orderId("testOrderId")
			.orderName("testOrderName")
			.totalAmount(100L)
			.method("testMethod")
			.orderNumber("testOrderNumber")
			.progress(Progress.ORDER)
			.payStatus(true)
			.estimatedTime(LocalDateTime.now())
			.inOut(1L)
			.message("testMessage")
			.store(store)
			.userInfo(userInfo)
			.cart(cart)
			.coupon(coupon)
			.build();

		assertThatCode(() -> {
			if (order.getTotalAmount() == null) {
				throw new AssertionError("Total amount should not be null");
			}
		}).doesNotThrowAnyException();
	}
}
