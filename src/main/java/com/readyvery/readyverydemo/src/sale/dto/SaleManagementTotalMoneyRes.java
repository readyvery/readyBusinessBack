package com.readyvery.readyverydemo.src.sale.dto;

import java.util.Optional;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SaleManagementTotalMoneyRes {

	private String message;
	private boolean success;
	private Optional<Long> totalMoney;
}
