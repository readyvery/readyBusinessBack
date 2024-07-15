package com.readyvery.readyverydemo.orderservice.getorder;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.readyvery.readyverydemo.domain.Progress;
import com.readyvery.readyverydemo.security.jwt.dto.CustomUserDetails;
import com.readyvery.readyverydemo.src.order.OrderController;
import com.readyvery.readyverydemo.src.order.OrderService;
import com.readyvery.readyverydemo.src.order.dto.OrderRegisterRes;

@WebMvcTest(controllers = OrderController.class) // OrderController에 대한 단위 테스트를 설정
@ExtendWith(MockitoExtension.class) // Mockito 확장을 사용하여 테스트 클래스에 대한 확장 지시
public class GetOrderControllerTest {

	@Autowired
	private MockMvc mockMvc; // MockMvc 객체를 자동 주입하여 테스트에서 사용

	@MockBean
	private OrderService orderServiceImpl; // OrderService를 MockBean으로 설정하여 목 객체로 사용

	// JpaMetamodelMappingContext를 MockBean으로 설정하여 목 객체로 사용
	@MockBean
	private JpaMetamodelMappingContext jpaMetamodelMappingContext;

	@Autowired
	private ObjectMapper objectMapper; // ObjectMapper 객체를 자동 주입하여 JSON 직렬화/역직렬화에 사용

	@Mock
	private CustomUserDetails customUserDetails; // CustomUserDetails 객체를 Mock으로 설정

	@BeforeEach
	public void setup(WebApplicationContext webApplicationContext) {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
			.build(); // WebApplicationContext를 사용하여 MockMvc 설정

		// CustomUserDetails의 getId() 메서드를 목킹하여 항상 1L을 반환하도록 설정
		when(customUserDetails.getId()).thenReturn(1L);

		// CustomUserDetails 객체를 사용하여 인증 토큰을 생성하고 SecurityContext에 설정
		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(customUserDetails,
			null, Collections.emptyList());
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}

	@Test
	@DisplayName("주문 반환 테스트")
	public void testGetOrder() throws Exception {
		// Arrange
		Long userId = 1L; // 사용자 ID로 사용할 값
		Progress status = Progress.ORDER; // 테스트에서 사용할 Progress 상태 값

		// 테스트에서 사용할 OrderRegisterRes 응답 객체를 생성
		OrderRegisterRes orderResponse =
			new OrderRegisterRes(1L, Collections.emptyList(), Collections.emptyList());

		// orderServiceImpl의 getOrders 메서드를 목킹하여 지정된 사용자 ID와 상태 값으로 호출 시 orderResponse를 반환하도록 설정
		when(orderServiceImpl.getOrders(eq(userId), eq(status))).thenReturn(orderResponse);

		// Act & Assert
		// GET 요청을 수행하고 응답 상태와 내용을 검증
		mockMvc.perform(get("/v1/order")
				.param("status", status.name())) // 상태 값을 쿼리 파라미터로 설정
			.andExpect(status().isOk()) // HTTP 200 상태를 기대
			// 응답 내용이 orderResponse와 동일한지 검증
			.andExpect(content().json(objectMapper.writeValueAsString(orderResponse)));
	}
}
