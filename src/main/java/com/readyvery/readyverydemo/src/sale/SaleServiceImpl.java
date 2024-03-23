package com.readyvery.readyverydemo.src.sale;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.readyvery.readyverydemo.domain.CeoInfo;
import com.readyvery.readyverydemo.domain.repository.OrderRepository;
import com.readyvery.readyverydemo.global.exception.BusinessLogicException;
import com.readyvery.readyverydemo.global.exception.ExceptionCode;
import com.readyvery.readyverydemo.src.ceo.CeoServiceFacade;
import com.readyvery.readyverydemo.src.sale.dto.SaleManagementDto;
import com.readyvery.readyverydemo.src.sale.dto.SaleManagementReq;
import com.readyvery.readyverydemo.src.sale.dto.SaleManagementRes;
import com.readyvery.readyverydemo.src.sale.dto.SaleManagementTotalMoneyReq;
import com.readyvery.readyverydemo.src.sale.dto.SaleManagementTotalMoneyRes;
import com.readyvery.readyverydemo.src.sale.dto.SaleManagementTotalOrderReq;
import com.readyvery.readyverydemo.src.sale.dto.SaleManagementTotalOrderRes;
import com.readyvery.readyverydemo.src.sale.dto.SaleMapper;
import com.readyvery.readyverydemo.src.sale.dto.TotalSaleRes;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class SaleServiceImpl implements SaleService {

	private final CeoServiceFacade ceoServiceFacade;
	private final SaleMapper saleMapper;
	private final OrderRepository orderRepository;

	@Override
	public TotalSaleRes getTotalSaleMoney(Long id) {
		CeoInfo ceoInfo = ceoServiceFacade.getCeoInfo(id);
		return saleMapper.totalSaleToTotalSaleRes(getTotalSale(ceoInfo.getStore().getId()));
	}

	@Override
	public SaleManagementRes getSaleManagementMoney(Long id, SaleManagementReq saleManagementReq) {

		CeoInfo ceoInfo = ceoServiceFacade.getCeoInfo(id);
		if (ceoInfo.getStore() == null) {
			throw new BusinessLogicException(ExceptionCode.STORE_NOT_FOUND);
		}
		List<SaleManagementDto> saleManagementList = getSaleMoneyManagement(ceoInfo.getStore().getId(),
			convertToDateTime(saleManagementReq.getMonday()));

		return SaleManagementRes.builder()
			.message("매출관리 조회 성공")
			.success(true)
			.saleManagementList(saleManagementList)
			.build();
	}

	// TODO: getWeekSaleManagementMoney, getMonthlySalesAmount 합치기
	@Override
	public SaleManagementTotalMoneyRes getWeekSaleManagementMoney(Long id,
		SaleManagementTotalMoneyReq saleManagementTotalMoneyReq) {
		CeoInfo ceoInfo = ceoServiceFacade.getCeoInfo(id);
		if (ceoInfo.getStore() == null) {
			throw new BusinessLogicException(ExceptionCode.STORE_NOT_FOUND);
		}
		Optional<Long> saleManagementTotal = sumTotalAmountByStoreIdForWeek(ceoInfo.getStore().getId(),
			getStartOfWeek(convertToDateTime(saleManagementTotalMoneyReq.getMonday())),
			getStartOfWeek(convertToDateTime(saleManagementTotalMoneyReq.getMonday())).plusDays(7));

		return SaleManagementTotalMoneyRes.builder()
			.message("해당 주차 총 매출 조회 성공")
			.success(true)
			.totalMoney(saleManagementTotal)
			.build();
	}

	@Override
	public SaleManagementTotalMoneyRes getMonthlySalesAmount(Long id,
		SaleManagementTotalMoneyReq saleManagementTotalMoneyReq) {
		CeoInfo ceoInfo = ceoServiceFacade.getCeoInfo(id);
		if (ceoInfo.getStore() == null) {
			throw new BusinessLogicException(ExceptionCode.STORE_NOT_FOUND);
		}
		Optional<Long> saleManagementTotal = sumTotalAmountByStoreIdForMonth(ceoInfo.getStore().getId(),
			getStartOfMonth(convertToDateTime(saleManagementTotalMoneyReq.getMonday())),
			getEndOfMonth(convertToDateTime(saleManagementTotalMoneyReq.getMonday())));

		return SaleManagementTotalMoneyRes.builder()
			.message("해당 월 총 매출 조회 성공")
			.success(true)
			.totalMoney(saleManagementTotal)
			.build();
	}

	@Override
	public SaleManagementTotalOrderRes getSaleManagementOrder(Long id,
		SaleManagementTotalOrderReq saleManagementTotalOrderReq) {

		CeoInfo ceoInfo = ceoServiceFacade.getCeoInfo(id);
		if (ceoInfo.getStore() == null) {
			throw new BusinessLogicException(ExceptionCode.STORE_NOT_FOUND);
		}
		Long saleManagementWeekTotal = countOrdersByStoreIdForWeek(ceoInfo.getStore().getId(),
			getStartOfWeek(convertToDateTime(saleManagementTotalOrderReq.getMonday())),
			getStartOfWeek(convertToDateTime(saleManagementTotalOrderReq.getMonday())).plusDays(7));
		Long saleManagementMonthTotal = countOrdersByStoreIdForMonth(ceoInfo.getStore().getId(),
			getStartOfMonth(convertToDateTime(saleManagementTotalOrderReq.getMonday())),
			getEndOfMonth(convertToDateTime(saleManagementTotalOrderReq.getMonday())));

		return SaleManagementTotalOrderRes.builder()
			.message("해당 주차, 월 총 주문 건수 조회 성공")
			.success(true)
			.totalWeekOrder(saleManagementWeekTotal)
			.totalMonthOrder(saleManagementMonthTotal)
			.build();
	}

	private Long getTotalSale(Long id) {
		return orderRepository.sumTotalAmountByStoreIdAndEstimatedTime(id).orElseThrow(
			() -> new BusinessLogicException(ExceptionCode.SALE_NOT_FOUND)
		);
	}

	public List<SaleManagementDto> getSaleMoneyManagement(Long id, LocalDateTime day) {
		LocalDateTime startDateTime = day;
		LocalDateTime endDateTime = day.plusDays(7);
		return getSaleManagementMoneyData(id, startDateTime, endDateTime);
	}

	public Optional<Long> sumTotalAmountByStoreIdForMonth(Long storeId, LocalDateTime startOfMonth,
		LocalDateTime endOfMonth) {
		return orderRepository.sumTotalAmountByStoreIdForMonth(storeId, startOfMonth, endOfMonth);
	}

	public Optional<Long> sumTotalAmountByStoreIdForWeek(Long storeId, LocalDateTime startOfWeek,
		LocalDateTime endOfWeek) {
		return orderRepository.sumTotalAmountByStoreIdForWeek(storeId, startOfWeek, endOfWeek);
	}

	public Long countOrdersByStoreIdForMonth(Long storeId, LocalDateTime startOfMonth,
		LocalDateTime endOfMonth) {
		return orderRepository.countOrdersByStoreIdForMonth(storeId, startOfMonth, endOfMonth);
	}

	public Long countOrdersByStoreIdForWeek(Long storeId, LocalDateTime startOfWeek,
		LocalDateTime endOfWeek) {
		return orderRepository.countOrdersByStoreIdForWeek(storeId, startOfWeek, endOfWeek);
	}

	public List<SaleManagementDto> getSaleManagementMoneyData(Long storeId, LocalDateTime startDate,
		LocalDateTime endDate) {
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

	// 주어진 날짜의 주 시작을 LocalDateTime으로 찾음
	public LocalDateTime getStartOfWeek(LocalDateTime dateTime) {
		return dateTime.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
	}

	public LocalDateTime getStartOfMonth(LocalDateTime dateTime) {
		return dateTime.with(TemporalAdjusters.firstDayOfMonth()).toLocalDate().atStartOfDay();
	}

	public LocalDateTime getEndOfMonth(LocalDateTime dateTime) {
		return dateTime.with(TemporalAdjusters.lastDayOfMonth()).toLocalDate().atTime(23, 59, 59);
	}
}
