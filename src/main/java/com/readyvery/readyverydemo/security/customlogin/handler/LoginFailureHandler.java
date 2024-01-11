package com.readyvery.readyverydemo.security.customlogin.handler;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.readyvery.readyverydemo.src.ceo.dto.CeoLoginRes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class LoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {

	private final ObjectMapper objectMapper;

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
		AuthenticationException exception) throws
		IOException {
		// 여기에 실패 처리 로직을 구현합니다.
		response.setContentType("application/json;charset=UTF-8");
		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		CeoLoginRes ceoLoginRes = CeoLoginRes.builder()
			.success(false)
			.message("로그인 실패")
			.build();

		try {
			String jsonResponse = objectMapper.writeValueAsString(ceoLoginRes);
			response.getWriter().write(jsonResponse);
		} catch (IOException e) {
			log.error("응답 작성 중 에러 발생", e);

		}
		log.info("로그인에 실패했습니다. 메시지: {}", exception.getMessage());
	}

}
