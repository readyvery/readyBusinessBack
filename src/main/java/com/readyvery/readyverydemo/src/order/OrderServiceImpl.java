package com.readyvery.readyverydemo.src.order;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import net.minidev.json.JSONObject;

import com.readyvery.readyverydemo.domain.CeoInfo;
import com.readyvery.readyverydemo.domain.Order;
import com.readyvery.readyverydemo.domain.Progress;
import com.readyvery.readyverydemo.domain.repository.CeoRepository;
import com.readyvery.readyverydemo.domain.repository.OrderRepository;
import com.readyvery.readyverydemo.global.exception.BusinessLogicException;
import com.readyvery.readyverydemo.global.exception.ExceptionCode;
import com.readyvery.readyverydemo.src.order.config.TossPaymentConfig;
import com.readyvery.readyverydemo.src.order.dto.OrderMapper;
import com.readyvery.readyverydemo.src.order.dto.OrderRegisterRes;
import com.readyvery.readyverydemo.src.order.dto.OrderStatusRes;
import com.readyvery.readyverydemo.src.order.dto.OrderStatusUpdateReq;
import com.readyvery.readyverydemo.src.order.dto.TosspaymentDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

	private final OrderRepository orderRepository;
	private final OrderMapper orderMapper;
	private final CeoRepository ceoRepository;
	private final TossPaymentConfig tosspaymentConfig;

	@Override
	public OrderRegisterRes getOrders(Long id, Progress progress) {
		CeoInfo ceoInfo = getCeoInfo(id);

		if (progress == null) {
			throw new BusinessLogicException(ExceptionCode.NOT_PROGRESS_ORDER);
		}

		List<Order> orders = orderRepository.findAllByProgressAndStoreId(progress, ceoInfo.getStore().getId());

		if (orders.isEmpty()) {
			throw new BusinessLogicException(ExceptionCode.NOT_FOUND_ORDER);
		}
		return orderMapper.orderToOrderRegisterRes(orders);
	}

	@Override
	public OrderStatusRes completeOrder(Long id, OrderStatusUpdateReq request) {
		CeoInfo ceoInfo = getCeoInfo(id);
		Order order = getOrder(request.getOrderId());

		verifyPostOrder(ceoInfo, order);
		verifyPostProgress(order, request);
		order.completeOrder(request.getStatus());
		orderRepository.save(order);
		return OrderStatusRes.builder()
			.success(true)
			.build();
	}

	@Override
	public OrderStatusRes cancelOrder(Long id, OrderStatusUpdateReq request) {
		CeoInfo ceoInfo = getCeoInfo(id);
		Order order = getOrder(request.getOrderId());

		verifyPostOrder(ceoInfo, order);
		verifyPostProgress(order, request);
		//order.cancelOrder(request.getStatus());
		orderRepository.save(order);
		return OrderStatusRes.builder()
			.success(true)
			.build();
	}

	@Override
	public void cancelTossPayment(Order order, OrderStatusUpdateReq request) {

		TosspaymentDto tosspaymentDto = requestTossPaymentCancel(order.getPaymentKey(), request.getRejectReason());

		applyCancelTosspaymentDto(order, tosspaymentDto);

	}

	private void verifyPostProgress(Order order, OrderStatusUpdateReq request) {
		if (order.getProgress() == Progress.ORDER && request.getStatus() == Progress.MAKE) { // 주문 -> 제조
			if (request.getTime() == null) {
				throw new BusinessLogicException(ExceptionCode.NOT_FOUND_TIME);
			}
			order.OrderTime(request.getTime());
			return;
		} else if (order.getProgress() == Progress.ORDER && request.getStatus() == Progress.CANCEL) {
			if (request.getRejectReason() == null) {
				throw new BusinessLogicException(ExceptionCode.NOT_FOUND_REJECT_REASON);
			}
			cancelTossPayment(order, request);
			return;
		} else if (order.getProgress() == Progress.MAKE && request.getStatus() == Progress.COMPLETE) { // 제조 -> 완료
			return;
		} else if (order.getProgress() == Progress.COMPLETE && request.getStatus() == Progress.PICKUP) { // 완료 -> 픽업
			return;
		} else {
			throw new BusinessLogicException(ExceptionCode.NOT_CHANGE_ORDER);
		}
	}

	private void verifyPostOrder(CeoInfo ceoInfo, Order order) {
		if (Objects.equals(order.getStore(), ceoInfo.getStore())) {
			return;
		}
		throw new BusinessLogicException(ExceptionCode.NOT_FOUND_ORDER);
	}

	private Order getOrder(String orderId) {
		return orderRepository.findByOrderId(orderId).orElseThrow(
			() -> new BusinessLogicException(ExceptionCode.NOT_FOUND_ORDER)
		);
	}

	private CeoInfo getCeoInfo(Long id) {
		return ceoRepository.findById(id).orElseThrow(
			() -> new BusinessLogicException(ExceptionCode.USER_NOT_FOUND)
		);
	}

	private void applyCancelTosspaymentDto(Order order, TosspaymentDto tosspaymentDto) {
		order.cancelOrder(Progress.CANCEL);
		order.cancelPayStatus();
		order.getReceipt().setCancels(tosspaymentDto.getCancels().toString());
		order.getReceipt().setStatus(tosspaymentDto.getStatus());
	}

	private TosspaymentDto requestTossPaymentCancel(String paymentKey, String rejectReason) {
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = makeTossHeader();
		JSONObject params = new JSONObject();

		params.put("cancelReason", rejectReason);

		try {
			return restTemplate.postForObject(TossPaymentConfig.PAYMENT_URL + paymentKey + "/cancel",
				new HttpEntity<>(params, headers),
				TosspaymentDto.class);
		} catch (Exception e) {
			System.out.println("e.getMessage() = " + e.getMessage());
			throw new BusinessLogicException(ExceptionCode.TOSS_PAYMENT_SUCCESS_FAIL);
		}
	}

	private HttpHeaders makeTossHeader() {
		HttpHeaders headers = new HttpHeaders();
		String encodedAuthKey = new String(
			Base64.getEncoder().encode((tosspaymentConfig.getTossSecretKey() + ":").getBytes(StandardCharsets.UTF_8)));
		headers.setBasicAuth(encodedAuthKey);
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		return headers;
	}

}
