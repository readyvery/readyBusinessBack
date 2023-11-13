package com.readyvery.readyverydemo.src.payment;

import com.readyvery.readyverydemo.src.payment.dto.PaymentReq;
import com.readyvery.readyverydemo.src.payment.dto.TossPaymentRes;
import com.readyvery.readyverydemo.src.payment.dto.toss.Payments;

public interface PaymentService {
	TossPaymentRes requestTossPayment(PaymentReq paymentReq);

	Payments tossPaymentSuccess(String paymentKey, String orderId, Long amount);
}
