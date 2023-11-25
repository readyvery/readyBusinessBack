package com.readyvery.readyverydemo.src.order.dto;

import com.readyvery.readyverydemo.domain.Progress;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrderCancelStatusUpdateReq {
	private String orderId;
	private Progress status;
	private String rejectReason;

}
