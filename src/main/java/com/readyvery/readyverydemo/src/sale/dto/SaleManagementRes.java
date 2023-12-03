package com.readyvery.readyverydemo.src.sale.dto;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SaleManagementRes {
	private String message;
	private boolean success;
	private List<SaleManagementDto> saleManagementList;
}
