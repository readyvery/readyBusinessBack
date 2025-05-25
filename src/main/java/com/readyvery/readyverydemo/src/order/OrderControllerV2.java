package com.readyvery.readyverydemo.src.order;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.readyvery.readyverydemo.domain.CeoInfo;
import com.readyvery.readyverydemo.security.jwt.dto.CustomUserDetails;
import com.readyvery.readyverydemo.src.ceo.CeoServiceFacade;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v2")
public class OrderControllerV2 {

	private final StoreSseEmitterManager storeSseEmitterManager;
	private final CeoServiceFacade ceoServiceFacade;

	@Operation(summary = "사장님 전용 주문 SSE",
		description = "Kafka로 전달되는 주문 상태 변경을 실시간 수신 (사장님 권한 + 가게 소유권 검증)",
		tags = "Order")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK")
	})
	@PreAuthorize("hasRole('CEO')")
	@GetMapping("/order/stream")
	public SseEmitter streamOrderUpdates(@AuthenticationPrincipal CustomUserDetails userDetails) {

		CeoInfo ceoInfo = ceoServiceFacade.getCeoInfo(userDetails.getId());
		// 검증 완료 후, storeSseEmitterManager에 등록
		return storeSseEmitterManager.createEmitterForStore(ceoInfo.getStore().getId());
	}
}
