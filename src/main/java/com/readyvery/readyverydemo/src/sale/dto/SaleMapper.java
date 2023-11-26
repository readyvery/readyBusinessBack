package com.readyvery.readyverydemo.src.sale.dto;

import org.springframework.stereotype.Component;

@Component
public class SaleMapper {

	public TotalSaleRes totalSaleToTotalSaleRes(Long totalSale) {
		return TotalSaleRes.builder()
			.message("총 매출 조회 성공")
			.success(true)
			.totalSale(totalSale)
			.build();
	}
}
