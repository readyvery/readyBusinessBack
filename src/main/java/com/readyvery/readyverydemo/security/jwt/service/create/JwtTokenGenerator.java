package com.readyvery.readyverydemo.security.jwt.service.create;

public interface JwtTokenGenerator {
	String generateAccessToken(String email, Long ceoId);

	String generateRefreshToken();
}
