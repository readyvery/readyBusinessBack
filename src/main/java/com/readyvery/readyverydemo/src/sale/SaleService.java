package com.readyvery.readyverydemo.src.sale;

import com.readyvery.readyverydemo.src.sale.dto.TotalSaleRes;

public interface SaleService {
	TotalSaleRes getTotalSaleMoney(Long id);
}
