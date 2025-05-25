package com.readyvery.readyverydemo.security.jwt.service.sendmanger;

import static com.readyvery.readyverydemo.config.JwtConfig.*;

import java.util.Optional;

import org.springframework.context.annotation.Configuration;

import com.auth0.jwt.JWT;
import com.readyvery.readyverydemo.config.JwtConfig;
import com.readyvery.readyverydemo.domain.Role;

import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
@Getter
@RequiredArgsConstructor
public class JwtTokenizer {

	private final TokenSendManager tokenSendManager;
	private final JwtConfig jwtConfig;

	public void addAccessTokenCookie(HttpServletResponse response, String accessToken) {
		tokenSendManager.addTokenCookie(response, jwtConfig.getAccessTokenName(), accessToken, "/",
			jwtConfig.getAccessTokenExpirationPeriod().intValue(), false);
	}

	public void addRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
		tokenSendManager.addTokenCookie(response, jwtConfig.getRefreshTokenName(), refreshToken,
			"/api/v1/refresh/token",
			jwtConfig.getRefreshTokenExpirationPeriod().intValue(), true);
	}

	public void addAccessRefreshTokenResponseBody(HttpServletResponse response, String accessToken,
		String refreshToken, Role role) {
		tokenSendManager.addTokenResponseBody(response, accessToken, refreshToken, role);

	}

	public Optional<String> verifyAccessToken(String accessToken) {
		return Optional.ofNullable(JWT.require(jwtConfig.getAlgorithm())
			.build()
			.verify(accessToken)
			.getClaim(EMAIL_CLAIM)
			.asString());
	}

}
