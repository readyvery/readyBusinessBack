package com.readyvery.readyverydemo.src.order;

import static com.readyvery.readyverydemo.global.Constant.*;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import net.minidev.json.JSONObject;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.exception.NurigoMessageNotReceivedException;
import net.nurigo.sdk.message.model.KakaoOption;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.service.DefaultMessageService;

import com.readyvery.readyverydemo.config.SolApiConfig;
import com.readyvery.readyverydemo.config.TossPaymentConfig;
import com.readyvery.readyverydemo.domain.CeoInfo;
import com.readyvery.readyverydemo.domain.Order;
import com.readyvery.readyverydemo.domain.Progress;
import com.readyvery.readyverydemo.domain.repository.OrderRepository;
import com.readyvery.readyverydemo.global.exception.BusinessLogicException;
import com.readyvery.readyverydemo.global.exception.ExceptionCode;
import com.readyvery.readyverydemo.src.ceo.CeoServiceFacade;
import com.readyvery.readyverydemo.src.order.dto.FailDto;
import com.readyvery.readyverydemo.src.order.dto.OrderMapper;
import com.readyvery.readyverydemo.src.order.dto.OrderRegisterRes;
import com.readyvery.readyverydemo.src.order.dto.OrderStatusRes;
import com.readyvery.readyverydemo.src.order.dto.OrderStatusUpdateReq;
import com.readyvery.readyverydemo.src.order.dto.TosspaymentDto;
import com.readyvery.readyverydemo.src.point.PointService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
	private final OrderRepository orderRepository;
	private final OrderMapper orderMapper;
	private final TossPaymentConfig tosspaymentConfig;
	private final SolApiConfig solApiConfig;
	private final CeoServiceFacade ceoServiceFacade;
	private final PointService pointService;

	@Override
	public OrderRegisterRes getOrders(Long id, Progress progress) {
		CeoInfo ceoInfo = ceoServiceFacade.getCeoInfo(id);

		if (progress == null) {
			throw new BusinessLogicException(ExceptionCode.NOT_PROGRESS_ORDER);
		}
		LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
		LocalDateTime endOfDay = startOfDay.plusDays(1).minusNanos(1);
		List<Order> orders = orderRepository.findAllByProgressAndStoreIdAndCreatedAtBetween(
			progress, ceoInfo.getStore().getId(), startOfDay, endOfDay);

		if (orders.isEmpty()) {
			return OrderRegisterRes.builder()
				.orders(Collections.emptyList())
				.build();
		}
		return orderMapper.orderToOrderRegisterRes(orders);
	}

	@Override
	@Transactional
	public OrderStatusRes completeOrder(Long id, OrderStatusUpdateReq request) {
		CeoInfo ceoInfo = ceoServiceFacade.getCeoInfo(id);
		Order order = getOrder(request.getOrderId());
		verifyPostOrder(ceoInfo, order);
		verifyPostProgress(order, request);
		order.completeOrder(request.getStatus());

		// 포인트 적립
		if (!order.getStore().getGrade().equals(Grade.EVENT)) {
			pointService.giveOrderPoint(order, request.getStatus());
		}

		orderRepository.save(order);

		sendCompleteMessage(order, request.getStatus());

		return OrderStatusRes.builder()
			.success(true)
			.build();
	}

	private void sendCompleteMessage(Order order, Progress status) {
		if (status == Progress.MAKE) {
			KakaoOption kakaoOption = makeKakaoOption(solApiConfig.getTempleteOrder());
			HashMap<String, String> variables = makeMakeVariables(order);
			sendMessage(kakaoOption, variables, order.getUserInfo().getPhone());
		}
		if (status == Progress.COMPLETE) {
			KakaoOption kakaoOption = makeKakaoOption(solApiConfig.getTempletePickup());
			HashMap<String, String> variables2 = makeCompeleteVariables(order);
			sendMessage(kakaoOption, variables2, order.getUserInfo().getPhone());
		}
	}

	private HashMap<String, String> makeCompeleteVariables(Order order) {
		HashMap<String, String> variables = new HashMap<>();
		variables.put("#{storeName}", order.getStore().getName());
		variables.put("#{orderName}", order.getOrderName());
		variables.put("#{orderNumber}", order.getOrderNumber());
		variables.put("#{orderId}", order.getOrderId());
		return variables;
	}

	private HashMap<String, String> makeMakeVariables(Order order) {
		HashMap<String, String> variables = new HashMap<>();
		variables.put("#{userName}", order.getUserInfo().getNickName());
		variables.put("#{orderDate}", order.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm")));
		variables.put("#{orderNumber}", order.getOrderNumber());
		variables.put("#{storeName}", order.getStore().getName());
		variables.put("#{orderName}", order.getOrderName());
		variables.put("#{estimatedTime}", order.getEstimatedTime().format(DateTimeFormatter.ofPattern("HH:mm")));
		variables.put("#{orderId}", order.getOrderId());
		return variables;
	}

	@Override
	@Transactional
	public OrderStatusRes cancelOrder(Long id, OrderStatusUpdateReq request) {
		CeoInfo ceoInfo = ceoServiceFacade.getCeoInfo(id);
		Order order = getOrder(request.getOrderId());

		verifyPostOrder(ceoInfo, order);
		verifyPostProgress(order, request);
		//order.cancelOrder(request.getStatus());
		pointService.cancelOrderPoint(order);
		orderRepository.save(order);

		// 카카오 알림톡 전송
		KakaoOption kakaoOption = makeKakaoOption(solApiConfig.getTempleteCancel());
		HashMap<String, String> variables = makeCancelVariables(order, request.getRejectReason());
		String phone = order.getUserInfo().getPhone();

		sendMessage(kakaoOption, variables, phone);
		return OrderStatusRes.builder()
			.success(true)
			.build();
	}

	private KakaoOption makeKakaoOption(String templateId) {
		KakaoOption kakaoOption = new KakaoOption();
		kakaoOption.setPfId(solApiConfig.getKakaoPfid());
		kakaoOption.setTemplateId(templateId);
		kakaoOption.setDisableSms(true);
		return kakaoOption;
	}

	private HashMap<String, String> makeCancelVariables(Order order, String rejectReason) {
		HashMap<String, String> variables = new HashMap<>();
		variables.put("#{storeName}", order.getStore().getName());
		variables.put("#{orderName}", order.getOrderName());
		variables.put("#{cancelReason}", rejectReason);
		return variables;
	}

	private void sendMessage(KakaoOption kakaoOption, HashMap<String, String> variables, String phone) {
		DefaultMessageService messageService = NurigoApp.INSTANCE.initialize(
			solApiConfig.getApiKey(),
			solApiConfig.getApiSecret(),
			SolApiConfig.SOLAPI_URL);

		kakaoOption.setVariables(variables);

		// Message 패키지가 중복될 경우 net.nurigo.sdk.message.model.Message로 치환하여 주세요
		Message message = new Message();
		message.setTo(phone);
		message.setKakaoOptions(kakaoOption);

		try {
			// send 메소드로 ArrayList<Message> 객체를 넣어도 동작합니다!
			messageService.send(message);
		} catch (NurigoMessageNotReceivedException exception) {
			// 발송에 실패한 메시지 목록을 확인할 수 있습니다!
			log.error(exception.getFailedMessageList());
		} catch (Exception exception) {
			// System.out.println(exception.getMessage());
		}
	}

	@Override
	public void cancelTossPayment(Order order, OrderStatusUpdateReq request) {
		TosspaymentDto tosspaymentDto = null;
		if (order.getAmount() > 0L) {
			tosspaymentDto = requestTossPaymentCancel(order.getPaymentKey(), request.getRejectReason());
		} else {
			tosspaymentDto = makeZeroPaymentCancelDto(request.getRejectReason());
		}

		applyCancelTosspaymentDto(order, tosspaymentDto);

	}

	private TosspaymentDto makeZeroPaymentCancelDto(String rejectReason) {
		return TosspaymentDto.builder()
			.cancels(",=" + rejectReason)
			.status("CANCELED")
			.build();
	}

	private void verifyPostProgress(Order order, OrderStatusUpdateReq request) {
		if (order.getProgress() == Progress.ORDER && request.getStatus() == Progress.MAKE) { // 주문 -> 제조
			if (request.getTime() == null) {
				throw new BusinessLogicException(ExceptionCode.NOT_FOUND_TIME);
			}
			order.orderTime(request.getTime());
		} else if (order.getProgress() == Progress.ORDER && request.getStatus() == Progress.CANCEL) {
			if (request.getRejectReason() == null) {
				throw new BusinessLogicException(ExceptionCode.NOT_FOUND_REJECT_REASON);
			}
			cancelTossPayment(order, request);
		} else if (order.getProgress() == Progress.MAKE && request.getStatus() == Progress.COMPLETE) { // 제조 -> 완료
		} else if (order.getProgress() == Progress.COMPLETE && request.getStatus() == Progress.PICKUP) { // 완료 -> 픽업
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

	private void applyCancelTosspaymentDto(Order order, TosspaymentDto tosspaymentDto) {
		order.cancelOrder(Progress.CANCEL);
		order.cancelPayStatus();
		order.getReceipt().setCancels(tosspaymentDto.getCancels().toString());
		order.getReceipt().setStatus(tosspaymentDto.getStatus());
		if (order.getCoupon() != null) {
			order.getCoupon().setUseCount(order.getCoupon().getUseCount() - 1);
		}
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
		}  catch (HttpClientErrorException e) {
			/*
			 * 취소 실패 시, 이미 취소된 거래라면 결제 정보 조회
			 * 취소된 정보 재적용
			 */
			if (e.getResponseBodyAs(FailDto.class).getCode()
				.equals(TOSS_RESPONSE_FAIL_CANCELED)) {
				return restTemplate.exchange(
						TossPaymentConfig.PAYMENT_URL + paymentKey,
						HttpMethod.GET,
						new HttpEntity<>(headers),
						TosspaymentDto.class)
					.getBody();
			}
			throw new BusinessLogicException(ExceptionCode.TOSS_PAYMENT_SUCCESS_FAIL);

		} catch (Exception e) {
			log.error("e.getMessage() = " + e.getMessage());
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
