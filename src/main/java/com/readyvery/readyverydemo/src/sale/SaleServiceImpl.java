package com.readyvery.readyverydemo.src.sale;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.readyvery.readyverydemo.domain.CeoInfo;
import com.readyvery.readyverydemo.domain.repository.CeoRepository;
import com.readyvery.readyverydemo.domain.repository.OrderRepository;
import com.readyvery.readyverydemo.global.exception.BusinessLogicException;
import com.readyvery.readyverydemo.global.exception.ExceptionCode;
import com.readyvery.readyverydemo.src.sale.dto.SaleManagementDto;
import com.readyvery.readyverydemo.src.sale.dto.SaleManagementReq;
import com.readyvery.readyverydemo.src.sale.dto.SaleManagementRes;
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

	@Override
	public SaleManagementRes getSaleManagementMoney(Long id, SaleManagementReq request) {

		CeoInfo ceoInfo = getCeoInfo(id);
		List<SaleManagementDto> saleManagementList = getSaleManagement(ceoInfo.getStore().getId(), request);

		return SaleManagementRes.builder()
			.message("매출관리 조회 성공")
			.success(true)
			.saleManagementList(saleManagementList)
			.build();
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

	private List<SaleManagementDto> getSaleManagement(Long id, SaleManagementReq request) {
		LocalDateTime startDateTime = convertToDateTime(request.getMonday());
		LocalDateTime endDateTime = convertToDateTime(request.getMonday()).plusDays(6);
		return getSaleManagementData(id, startDateTime, endDateTime);
	}

	public List<SaleManagementDto> getSaleManagementData(Long storeId, LocalDateTime startDate, LocalDateTime endDate) {
		List<SaleManagementDto> queryResult = orderRepository.sumTotalAmountPerDayBetweenDates(storeId, startDate,
			endDate);
		List<SaleManagementDto> resultWithAllDays = initializeWeekData();

		Map<String, SaleManagementDto> resultMap = queryResult.stream()
			.collect(Collectors.toMap(SaleManagementDto::getDay, Function.identity()));

		resultWithAllDays.forEach(dayDto -> {
			if (resultMap.containsKey(dayDto.getDay())) {
				dayDto.setSale(resultMap.get(dayDto.getDay()).getSale());
			}
		});

		return resultWithAllDays;
	}

	private List<SaleManagementDto> initializeWeekData() {
		return Arrays.asList(
			new SaleManagementDto("Monday", 0L),
			new SaleManagementDto("Tuesday", 0L),
			new SaleManagementDto("Wednesday", 0L),
			new SaleManagementDto("Thursday", 0L),
			new SaleManagementDto("Friday", 0L),
			new SaleManagementDto("Saturday", 0L),
			new SaleManagementDto("Sunday", 0L)
		);
	}

	public LocalDateTime convertToDateTime(String dateStr) {
		LocalDate date = LocalDate.parse(dateStr);
		return date.atStartOfDay(); // 자정 시간으로 설정
	}
}
