package com.readyvery.readyverydemo.security.jwt.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@Getter
public class JwtTokenizer {

	public static final String ACCESS_TOKEN_SUBJECT = "AccessToken";
	public static final String REFRESH_TOKEN_SUBJECT = "RefreshToken";
	public static final String EMAIL_CLAIM = "email";
	public static final String USER_NUMBER = "userNumber";

	private String secretKey;
	private Long accessTokenExpirationPeriod;
	private Long refreshTokenExpirationPeriod;
	private Algorithm algorithm;

	private String accessCookie;
	private String refreshCookie;
	private String frontendUrl;
	private String cookieDomain;

	@Value("${jwt.secretKey}")
	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
		initializeAlgorithm();
	}

	@Value("${jwt.access.expiration}")
	public void setAccessTokenExpirationPeriod(Long accessTokenExpirationPeriod) {
		this.accessTokenExpirationPeriod = accessTokenExpirationPeriod;
	}

	@Value("${jwt.refresh.expiration}")
	public void setRefreshTokenExpirationPeriod(Long refreshTokenExpirationPeriod) {
		this.refreshTokenExpirationPeriod = refreshTokenExpirationPeriod;
	}

	@Value("${jwt.access.cookie}")
	public void setAccessCookie(String accessCookie) {
		this.accessCookie = accessCookie;
	}

	@Value("${jwt.refresh.cookie}")
	public void setRefreshCookie(String refreshCookie) {
		this.refreshCookie = refreshCookie;
	}

	@Value("${jwt.redirect-uri}")
	public void setFrontendUrl(String frontendUrl) {
		this.frontendUrl = frontendUrl;
	}

	@Value("${jwt.refresh.cookie.domain}")
	public void setCookieDomain(String cookieDomain) {
		this.cookieDomain = cookieDomain;
	}

	public String generateAccessToken(String email, Long ceoId) {
		Instant now = Instant.now();
		Instant expirationTime = now.plus(accessTokenExpirationPeriod, ChronoUnit.SECONDS);
		return JWT.create()
			.withSubject(ACCESS_TOKEN_SUBJECT)
			.withExpiresAt(Date.from(expirationTime))
			.withClaim(EMAIL_CLAIM, email)
			.withClaim(USER_NUMBER, ceoId)
			.sign(algorithm);
	}

	public String generateRefreshToken() {
		Instant now = Instant.now();
		Instant expirationTime = now.plus(refreshTokenExpirationPeriod, ChronoUnit.SECONDS);
		return JWT.create()
			.withSubject(REFRESH_TOKEN_SUBJECT)
			.withExpiresAt(Date.from(expirationTime))
			.sign(algorithm);
	}

	public void setAccessTokenCookie(HttpServletResponse response, String accessToken) {
		Cookie accessTokenCookie = new Cookie(accessCookie, accessToken);
		accessTokenCookie.setPath("/");
		accessTokenCookie.setDomain(cookieDomain);
		accessTokenCookie.setMaxAge(accessTokenExpirationPeriod.intValue());
		response.addCookie(accessTokenCookie);
	}

	public void setRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
		Cookie refreshTokenCookie = new Cookie(refreshCookie, refreshToken);
		refreshTokenCookie.setHttpOnly(true);
		refreshTokenCookie.setPath("/api/v1/refresh/token");
		refreshTokenCookie.setDomain(cookieDomain);
		refreshTokenCookie.setMaxAge(refreshTokenExpirationPeriod.intValue());
		response.addCookie(refreshTokenCookie);
	}

	public Optional<String> verifyAccessToken(String accessToken) {
		return Optional.ofNullable(JWT.require(algorithm)
			.build()
			.verify(accessToken)
			.getClaim(EMAIL_CLAIM)
			.asString());
	}

	private void initializeAlgorithm() {
		if (secretKey != null && !secretKey.isEmpty()) {
			algorithm = Algorithm.HMAC512(secretKey);
		}
	}
}
