package com.readyvery.readyverydemo.security.customlogin.handler;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import com.readyvery.readyverydemo.redis.dao.RefreshToken;
import com.readyvery.readyverydemo.redis.repository.RefreshTokenRepository;
import com.readyvery.readyverydemo.security.jwt.service.JwtService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	private final JwtService jwtService;
	private final RefreshTokenRepository refreshTokenRepository;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) {
		String email = extractCeoname(authentication);
		String accessToken = jwtService.createAccessToken(email);
		String refreshToken = jwtService.createRefreshToken();

		jwtService.sendAccessAndRefreshToken(response, accessToken, refreshToken);

		RefreshToken token = RefreshToken.builder()
			.id(email)
			.refreshToken(refreshToken)
			.build();
		refreshTokenRepository.save(token);
	}

	private String extractCeoname(Authentication authentication) {
		UserDetails userDetails = (UserDetails)authentication.getPrincipal();
		return userDetails.getUsername();
	}
}
