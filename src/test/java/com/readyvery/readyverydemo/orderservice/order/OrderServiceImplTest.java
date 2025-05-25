package com.readyvery.readyverydemo.orderservice.order;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.readyvery.readyverydemo.domain.CeoInfo;
import com.readyvery.readyverydemo.domain.Order;
import com.readyvery.readyverydemo.domain.Progress;
import com.readyvery.readyverydemo.domain.Store;
import com.readyvery.readyverydemo.domain.repository.OrderRepository;
import com.readyvery.readyverydemo.global.exception.BusinessLogicException;
import com.readyvery.readyverydemo.global.exception.ExceptionCode;
import com.readyvery.readyverydemo.src.ceo.CeoServiceFacade;
import com.readyvery.readyverydemo.src.order.OrderServiceImpl;
import com.readyvery.readyverydemo.src.order.dto.OrderMapper;
import com.readyvery.readyverydemo.src.order.dto.OrderStatusRes;
import com.readyvery.readyverydemo.src.order.dto.OrderStatusUpdateReq;
import com.readyvery.readyverydemo.src.point.PointService;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

	@Mock
	private OrderRepository orderRepository;

	@Mock
	private OrderMapper orderMapper;

	@Mock
	private CeoServiceFacade ceoServiceFacade;

	@Mock
	private PointService pointService;

	@InjectMocks
	private OrderServiceImpl orderServiceImpl;

	static Stream<Arguments> orderStatusProvider() {
		return Stream.of(
			Arguments.of(Progress.ORDER, Progress.MAKE),
			Arguments.of(Progress.ORDER, Progress.CANCEL),
			Arguments.of(Progress.MAKE, Progress.COMPLETE),
			Arguments.of(Progress.COMPLETE, Progress.PICKUP)
		);
	}

	@DisplayName("새 주문을 생성하고 저장한다.")
	@Test
	void createOrder() {
		CeoInfo ceoInfo = CeoInfo.builder().store(Store.builder().id(1L).build()).build();
		when(ceoServiceFacade.getCeoInfo(anyLong())).thenReturn(ceoInfo);

		Order order = Order.builder()
			.store(ceoInfo.getStore())
			.totalAmount(100L)
			.estimatedTime(LocalDateTime.now())
			.createdAt(LocalDateTime.now())
			.lastModifiedAt(LocalDateTime.now())
			.build();
		when(orderRepository.save(any(Order.class))).thenReturn(order);

		OrderStatusUpdateReq request = new OrderStatusUpdateReq(1L, Progress.ORDER, 30L, "Test reason");

		OrderStatusRes response = orderServiceImpl.completeOrder(1L, request);

		assertAll(
			() -> assertThat(response.isSuccess()).isTrue(),
			() -> verify(orderRepository, times(1)).save(any(Order.class))
		);
	}

	@DisplayName("주문 상태에 따라 주문을 업데이트한다.")
	@MethodSource("orderStatusProvider")
	@ParameterizedTest
	void updateOrderStatus(Progress currentStatus, Progress newStatus) {
		Store store = Store.builder().id(1L).build();
		CeoInfo ceoInfo = CeoInfo.builder().store(store).build();
		when(ceoServiceFacade.getCeoInfo(anyLong())).thenReturn(ceoInfo);

		Order order = Order.builder()
			.store(store)
			.progress(currentStatus)
			.totalAmount(100L)
			.build();
		when(orderRepository.findByOrderId(anyString())).thenReturn(Optional.of(order));
		when(orderRepository.save(any(Order.class))).thenReturn(order);

		OrderStatusUpdateReq request = new OrderStatusUpdateReq(1L, newStatus, 30L, "Test reason");

		OrderStatusRes response = orderServiceImpl.completeOrder(1L, request);

		assertAll(
			() -> assertThat(response.isSuccess()).isTrue(),
			() -> verify(orderRepository, times(1)).save(any(Order.class))
		);
	}

	@DisplayName("주문을 취소하고 결제 취소를 적용한다.")
	@Test
	void cancelOrder() {
		Store store = Store.builder().id(1L).build();
		CeoInfo ceoInfo = CeoInfo.builder().store(store).build();
		when(ceoServiceFacade.getCeoInfo(anyLong())).thenReturn(ceoInfo);

		Order order = Order.builder()
			.store(store)
			.progress(Progress.ORDER)
			.totalAmount(100L)
			.paymentKey("testPaymentKey")
			.build();
		when(orderRepository.findByOrderId(anyString())).thenReturn(Optional.of(order));
		when(orderRepository.save(any(Order.class))).thenReturn(order);

		OrderStatusUpdateReq request = new OrderStatusUpdateReq(1L, Progress.CANCEL, null, "Customer requested");

		OrderStatusRes response = orderServiceImpl.cancelOrder(1L, request);

		assertAll(
			() -> assertThat(response.isSuccess()).isTrue(),
			() -> verify(orderRepository, times(1)).save(any(Order.class)),
			() -> verify(pointService, times(1)).cancelOrderPoint(any(Order.class))
		);
	}

	@DisplayName("존재하지 않는 주문을 취소하려 할 때 예외를 던진다.")
	@Test
	void cancelOrderThrowsExceptionForNonExistentOrder() {
		when(orderRepository.findByOrderId(anyString())).thenReturn(Optional.empty());

		OrderStatusUpdateReq request = new OrderStatusUpdateReq(1L, Progress.CANCEL, null, "Customer requested");

		assertThatThrownBy(() -> orderServiceImpl.cancelOrder(1L, request))
			.isInstanceOf(BusinessLogicException.class)
			.hasMessageContaining(ExceptionCode.NOT_FOUND_ORDER.getMessage());

		verify(orderRepository, never()).save(any(Order.class));
	}

	// 더 많은 테스트 케이스를 추가하여 OrderServiceImpl의 기능을 검증할 수 있습니다.
}

