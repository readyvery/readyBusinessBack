package com.readyvery.readyverydemo.src.sale.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SaleManagementTotalOrderRes {
	private String message;
	private boolean success;
	private Long totalWeekOrder;
	private Long totalMonthOrder;

}
