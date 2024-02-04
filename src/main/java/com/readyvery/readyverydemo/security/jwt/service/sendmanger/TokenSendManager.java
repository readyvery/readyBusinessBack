package com.readyvery.readyverydemo.security.jwt.service.sendmanger;

import java.io.IOException;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.readyvery.readyverydemo.domain.Role;
import com.readyvery.readyverydemo.security.jwt.config.JwtConfig;
import com.readyvery.readyverydemo.src.ceo.dto.CeoLoginRes;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Component
@Slf4j
public class TokenSendManager {
	private final JwtConfig jwtConfig;
	private final ObjectMapper objectMapper;

	public void addTokenCookie(HttpServletResponse response, String name, String value, String path, int maxAge,
		boolean httpOnly) {
		Cookie cookie = new Cookie(name, value);
		cookie.setHttpOnly(httpOnly);
		cookie.setPath(path);
		cookie.setDomain(jwtConfig.getCookieDomain());
		cookie.setMaxAge(maxAge);
		response.addCookie(cookie);

	}

	public void addTokenResponseBody(HttpServletResponse response, String accessToken, String refreshToken, Role role) {

		// JSON 응답 생성 및 전송

		response.setContentType("application/json;charset=UTF-8");
		response.setStatus(HttpServletResponse.SC_OK);
		CeoLoginRes ceoLoginRes = CeoLoginRes.builder()
			.success(true)
			.message("로그인 성공")
			.accessToken(accessToken)
			.refreshToken(refreshToken)
			.role(role)
			.build();

		try {
			String jsonResponse = objectMapper.writeValueAsString(ceoLoginRes);
			response.getWriter().write(jsonResponse);
		} catch (IOException e) {
			log.error("응답 작성 중 에러 발생", e);
		}
	}

}
