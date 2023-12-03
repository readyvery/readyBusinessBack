package com.readyvery.readyverydemo.src.sale;

import com.readyvery.readyverydemo.src.sale.dto.SaleManagementReq;
import com.readyvery.readyverydemo.src.sale.dto.SaleManagementRes;
import com.readyvery.readyverydemo.src.sale.dto.TotalSaleRes;

public interface SaleService {
	TotalSaleRes getTotalSaleMoney(Long id);

	SaleManagementRes getSaleManagementMoney(Long id, SaleManagementReq request);
}
