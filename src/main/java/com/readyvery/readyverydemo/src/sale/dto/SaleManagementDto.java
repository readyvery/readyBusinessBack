package com.readyvery.readyverydemo.src.sale.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class SaleManagementDto {
	private String day;
	private Long sale;
}
