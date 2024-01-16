package com.readyvery.readyverydemo.security.jwt.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import com.auth0.jwt.algorithms.Algorithm;

import lombok.Getter;

@Configuration
@Getter
public class JwtConfig {

	private String secretKey;
	private Long accessTokenExpirationPeriod;
	private Long refreshTokenExpirationPeriod;
	private Algorithm algorithm;

	private String accessTokenName;
	private String refreshTokenName;
	private String frontendUrl;
	private String cookieDomain;

	public static final String ACCESS_TOKEN_SUBJECT = "AccessToken";
	public static final String REFRESH_TOKEN_SUBJECT = "RefreshToken";
	public static final String EMAIL_CLAIM = "email";
	public static final String USER_NUMBER = "userNumber";
	public static final String BEARER = "Bearer ";
	public static final String AUTHORIZATION = "Authorization";

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
	public void setAccessTokenName(String accessTokenName) {
		this.accessTokenName = accessTokenName;
	}

	@Value("${jwt.refresh.cookie}")
	public void setRefreshTokenName(String refreshTokenName) {
		this.refreshTokenName = refreshTokenName;
	}

	@Value("${jwt.redirect-uri}")
	public void setFrontendUrl(String frontendUrl) {
		this.frontendUrl = frontendUrl;
	}

	@Value("${jwt.refresh.cookie.domain}")
	public void setCookieDomain(String cookieDomain) {
		this.cookieDomain = cookieDomain;
	}

	private void initializeAlgorithm() {
		if (secretKey != null && !secretKey.isEmpty()) {
			algorithm = Algorithm.HMAC512(secretKey);
		}
	}
}
