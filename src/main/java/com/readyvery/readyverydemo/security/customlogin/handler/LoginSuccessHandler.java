package com.readyvery.readyverydemo.security.customlogin.handler;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import com.readyvery.readyverydemo.domain.CeoInfo;
import com.readyvery.readyverydemo.domain.repository.CeoRepository;
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
	private final CeoRepository ceoRepository;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) {
		CeoInfo ceoInfo = extractCeoInfo(authentication);

		String accessToken = jwtService.createAccessToken(ceoInfo.getEmail());
		String refreshToken = jwtService.createRefreshToken();

		jwtService.sendAccessAndRefreshToken(response, accessToken, refreshToken, ceoInfo.getRole());

		RefreshToken token = RefreshToken.builder()
			.id(ceoInfo.getEmail())
			.refreshToken(refreshToken)
			.build();
		refreshTokenRepository.save(token);
	}

	private CeoInfo extractCeoInfo(Authentication authentication) {
		UserDetails userDetails = (UserDetails)authentication.getPrincipal();
		return ceoRepository.findByEmail(userDetails.getUsername()).orElseThrow();
	}

}
