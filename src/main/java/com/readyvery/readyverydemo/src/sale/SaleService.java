package com.readyvery.readyverydemo.src.sale;

import com.readyvery.readyverydemo.src.sale.dto.SaleManagementReq;
import com.readyvery.readyverydemo.src.sale.dto.SaleManagementRes;
import com.readyvery.readyverydemo.src.sale.dto.SaleManagementTotalMoneyReq;
import com.readyvery.readyverydemo.src.sale.dto.SaleManagementTotalMoneyRes;
import com.readyvery.readyverydemo.src.sale.dto.SaleManagementTotalOrderReq;
import com.readyvery.readyverydemo.src.sale.dto.SaleManagementTotalOrderRes;
import com.readyvery.readyverydemo.src.sale.dto.TotalSaleRes;

public interface SaleService {
	TotalSaleRes getTotalSaleMoney(Long id);

	SaleManagementRes getSaleManagementMoney(Long id, SaleManagementReq saleManagementReq);

	SaleManagementTotalMoneyRes getWeekSaleManagementMoney(Long id,
		SaleManagementTotalMoneyReq saleManagementTotalMoneyReq);

	SaleManagementTotalMoneyRes getMonthlySalesAmount(Long id, SaleManagementTotalMoneyReq saleManagementTotalMoneyReq);

	SaleManagementTotalOrderRes getSaleManagementOrder(Long id,
		SaleManagementTotalOrderReq saleManagementTotalOrderReq);

}
