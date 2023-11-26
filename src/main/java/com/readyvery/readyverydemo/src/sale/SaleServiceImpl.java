package com.readyvery.readyverydemo.src.sale;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.readyvery.readyverydemo.domain.CeoInfo;
import com.readyvery.readyverydemo.domain.repository.CeoRepository;
import com.readyvery.readyverydemo.domain.repository.OrderRepository;
import com.readyvery.readyverydemo.global.exception.BusinessLogicException;
import com.readyvery.readyverydemo.global.exception.ExceptionCode;
import com.readyvery.readyverydemo.src.sale.dto.SaleMapper;
import com.readyvery.readyverydemo.src.sale.dto.TotalSaleRes;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class SaleServiceImpl implements SaleService {

	private final CeoRepository ceoRepository;
	private final SaleMapper saleMapper;
	private final OrderRepository orderRepository;

	@Override
	public TotalSaleRes getTotalSaleMoney(Long id) {
		CeoInfo ceoInfo = getCeoInfo(id);
		return saleMapper.totalSaleToTotalSaleRes(getTotalSale(ceoInfo.getStore().getId()));
	}

	private CeoInfo getCeoInfo(Long id) {
		return ceoRepository.findById(id).orElseThrow(
			() -> new BusinessLogicException(ExceptionCode.USER_NOT_FOUND)
		);
	}

	private Long getTotalSale(Long id) {
		return orderRepository.sumTotalAmountByStoreIdAndEstimatedTime(id).orElseThrow(
			() -> new BusinessLogicException(ExceptionCode.SALE_NOT_FOUND)
		);
	}
}
