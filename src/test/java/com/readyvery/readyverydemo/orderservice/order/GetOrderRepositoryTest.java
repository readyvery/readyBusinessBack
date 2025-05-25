package com.readyvery.readyverydemo.orderservice.order;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.readyvery.readyverydemo.domain.Order;
import com.readyvery.readyverydemo.domain.Progress;
import com.readyvery.readyverydemo.domain.Store;
import com.readyvery.readyverydemo.domain.repository.OrderRepository;
import com.readyvery.readyverydemo.src.sale.dto.SaleManagementDto;

import jakarta.persistence.EntityManager;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class GetOrderRepositoryTest {

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private EntityManager entityManager;

	@DisplayName("ID로 모든 주문을 조회한다.")
	@Test
	void testFindAllById() {
		// given
		Order order = Order.builder()
			.id(1L)
			.createdAt(LocalDateTime.now())
			.lastModifiedAt(LocalDateTime.now())
			.build();
		entityManager.persist(order);
		entityManager.flush();
		entityManager.clear();

		// when
		List<Order> orders = orderRepository.findAllById(1L);

		// then
		assertThat(orders).hasSize(1);
	}

	@DisplayName("진행 상태, 상점 ID, 생성 일자 범위로 주문을 조회한다.")
	@Test
	void testFindAllByProgressAndStoreIdAndCreatedAtBetween() {
		// given
		LocalDateTime now = LocalDateTime.now();
		Store store = Store.builder()
			.id(1L)
			.build();
		Order order = Order.builder()
			.progress(Progress.ORDER)
			.store(store)
			.createdAt(now)
			.lastModifiedAt(now)
			.build();
		entityManager.persist(order);
		entityManager.flush();
		entityManager.clear();

		// when
		List<Order> orders = orderRepository.findAllByProgressAndStoreIdAndCreatedAtBetween(
			Progress.ORDER, 1L, now.minusDays(1), now.plusDays(1));

		// then
		assertThat(orders).hasSize(1);
	}

	@DisplayName("주문 ID로 주문을 조회한다.")
	@Test
	@Transactional
	void testFindByOrderId() {
		// given
		Order order = Order.builder()
			.orderId("12345")
			.createdAt(LocalDateTime.now())
			.lastModifiedAt(LocalDateTime.now())
			.build();
		entityManager.persist(order);
		entityManager.flush();
		entityManager.clear();

		// when
		Optional<Order> foundOrder = orderRepository.findByOrderId("12345");

		// then
		assertThat(foundOrder).isPresent();
	}

	@DisplayName("상점 ID와 예상 시간으로 총 금액을 합산한다.")
	@Test
	void testSumTotalAmountByStoreIdAndEstimatedTime() {
		// given
		LocalDateTime fixedTime = LocalDateTime.now().withNano(0); // 고정된 시간을 설정합니다.

		Store store = Store.builder().id(1L)
			.name("Test Store")
			.address("123 Test Street")
			.phone("123-456-7890")
			.time("09:00-21:00")
			.status(true)
			.build();
		store = entityManager.merge(store); // Store 엔티티를 영속성 컨텍스트에 병합합니다.

		Order order = Order.builder()
			.store(store)
			.totalAmount(100L)
			.estimatedTime(fixedTime)
			.createdAt(fixedTime)
			.lastModifiedAt(fixedTime)
			.build();
		entityManager.persist(order);
		entityManager.flush();
		entityManager.clear();

		// when
		Optional<Long> totalAmount = orderRepository.sumTotalAmountByStoreIdAndEstimatedTime(store.getId());

		// then
		assertThat(totalAmount).isPresent().hasValue(100L);
	}

	@DisplayName("상점 ID와 날짜 범위로 일별 총 금액을 합산한다.")
	@Test
	void testSumTotalAmountPerDayBetweenDates() {
		// given
		LocalDateTime now = LocalDateTime.now();
		Order order = Order.builder()
			.store(Store.builder().id(1L).build())
			.totalAmount(100L)
			.estimatedTime(now)
			.createdAt(now)
			.lastModifiedAt(now)
			.build();
		entityManager.persist(order);
		entityManager.flush();
		entityManager.clear();

		// when
		List<SaleManagementDto> sales = orderRepository.sumTotalAmountPerDayBetweenDates(1L, now.minusDays(1),
			now.plusDays(1));

		// then
		assertThat(sales).isNotEmpty();
	}

	@DisplayName("상점 ID와 월간 범위로 총 금액을 합산한다.")
	@Test
	void testSumTotalAmountByStoreIdForMonth() {
		// given
		LocalDateTime now = LocalDateTime.now();
		Order order = Order.builder()
			.store(Store.builder().id(1L).build())
			.totalAmount(100L)
			.createdAt(now)
			.estimatedTime(now)
			.lastModifiedAt(now)
			.build();
		entityManager.persist(order);
		entityManager.flush();
		entityManager.clear();

		System.out.println("Order created at: " + order.getCreatedAt());
		System.out.println("Order estimated time: " + order.getEstimatedTime());

		// when
		LocalDateTime startOfMonth = now.withDayOfMonth(1);
		LocalDateTime endOfMonth = now.withDayOfMonth(now.toLocalDate().lengthOfMonth());
		System.out.println("Querying from " + startOfMonth + " to " + endOfMonth);

		Optional<Long> totalAmount = orderRepository.sumTotalAmountByStoreIdForMonth(1L, startOfMonth, endOfMonth);

		// then
		assertThat(totalAmount).isPresent().hasValue(100L);
	}
}
