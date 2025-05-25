package com.readyvery.readyverydemo.src.store.dto;

import lombok.Data;

@Data
public class OrderUpdateMessage {
	private String orderId;  // 주문 식별자
	private Long storeId;    // 어떤 가게의 주문인지
	private String status;   // 주문 상태 (ORDER, MAKE, COMPLETE 등)
	private String message;  // 기타 메시지
}
