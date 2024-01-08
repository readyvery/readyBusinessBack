package com.readyvery.readyverydemo.security.customlogin.handler;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.readyvery.readyverydemo.domain.repository.CeoRepository;
import com.readyvery.readyverydemo.security.jwt.service.JwtService;
import com.readyvery.readyverydemo.security.jwt.service.JwtTokenizer;
import com.readyvery.readyverydemo.src.ceo.dto.CeoLoginRes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	private final JwtService jwtService;
	private final CeoRepository ceoRepository;
	private final JwtTokenizer jwtTokenizer;
	private final ObjectMapper objectMapper;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) {
		String email = extractCeoname(authentication);
		String accessToken = jwtService.createAccessToken(email);
		String refreshToken = jwtService.createRefreshToken();

		jwtService.sendAccessAndRefreshToken(response, accessToken, refreshToken);

		ceoRepository.findByEmail(email)
			.ifPresent(ceoInfo -> {
				ceoInfo.updateRefresh(refreshToken);
				ceoRepository.saveAndFlush(ceoInfo);
			});

		// JSON 응답 생성 및 전송

		response.setContentType("application/json;charset=UTF-8");
		response.setStatus(HttpServletResponse.SC_OK);
		CeoLoginRes ceoLoginRes = CeoLoginRes.builder()
			.success(true)
			.message("로그인 성공")
			.build();

		try {
			String jsonResponse = objectMapper.writeValueAsString(ceoLoginRes);
			response.getWriter().write(jsonResponse);
		} catch (IOException e) {
			log.error("응답 작성 중 에러 발생", e);
		}
		log.info("로그인에 성공하였습니다. 이메일 : {}", email);
		log.info("로그인에 성공하였습니다. AccessToken : {}", accessToken);
		log.info("발급된 AccessToken 만료 기간 : {}", jwtTokenizer.getAccessTokenExpirationPeriod());
	}

	private String extractCeoname(Authentication authentication) {
		UserDetails userDetails = (UserDetails)authentication.getPrincipal();
		return userDetails.getUsername();
	}
}
