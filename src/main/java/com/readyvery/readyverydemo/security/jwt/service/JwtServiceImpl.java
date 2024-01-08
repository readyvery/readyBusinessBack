package com.readyvery.readyverydemo.security.jwt.service;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.readyvery.readyverydemo.domain.CeoInfo;
import com.readyvery.readyverydemo.domain.repository.CeoRepository;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Getter
@Slf4j
public class JwtServiceImpl implements JwtService {

	private final CeoRepository ceoRepository;
	private final JwtTokenizer jwtTokenizer;

	/**
	 * AccessToken 생성 메소드
	 */
	@Override
	public String createAccessToken(String email) {
		CeoInfo ceoInfo = ceoRepository.findByEmail(email)
			.orElseThrow(() -> new IllegalArgumentException("이메일에 해당하는 유저가 없습니다."));
		return jwtTokenizer.generateAccessToken(email, ceoInfo.getId());
	}

	/**
	 * RefreshToken 생성
	 * RefreshToken은 Claim에 email도 넣지 않으므로 withClaim() X
	 */
	@Override
	public String createRefreshToken() {

		return jwtTokenizer.generateRefreshToken();
	}

	/**
	 * AccessToken + RefreshToken 헤더에 실어서 보내기
	 */
	@Override
	public void sendAccessAndRefreshToken(HttpServletResponse response, String accessToken, String refreshToken) {

		response.setStatus(HttpServletResponse.SC_OK);

		jwtTokenizer.setAccessTokenCookie(response, accessToken);
		jwtTokenizer.setRefreshTokenCookie(response, refreshToken);
		log.info("Access Token, Refresh Token 헤더 설정 완료");
	}

	/**
	 * 쿠키에서 RefreshToken 추출
	 */
	@Override
	public Optional<String> extractRefreshTokenFromCookies(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			return Arrays.stream(cookies)
				.filter(cookie -> jwtTokenizer.getRefreshCookie().equals(cookie.getName())) // 올바른 필터링 조건
				.findFirst()
				.map(Cookie::getValue);
		}
		return Optional.empty();
	}

	/**
	 * 쿠키에서 AccessToken 추출
	 */
	@Override
	public Optional<String> extractAccessTokenFromCookies(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			return Arrays.stream(cookies)
				.filter(cookie -> jwtTokenizer.getAccessCookie().equals(cookie.getName())) // 올바른 필터링 조건
				.findFirst()
				.map(Cookie::getValue);
		}
		return Optional.empty();
	}

	/**
	 * AccessToken에서 Email 추출
	 * 추출 전에 JWT.require()로 검증기 생성
	 * verify로 AceessToken 검증 후
	 * 유효하다면 getClaim()으로 이메일 추출
	 * 유효하지 않다면 빈 Optional 객체 반환
	 */
	@Override
	public Optional<String> extractEmail(String accessToken) {
		try {
			// 토큰 유효성 검사하는 데에 사용할 알고리즘이 있는 JWT verifier builder 반환
			return jwtTokenizer.verifyAccessToken(accessToken);
		} catch (Exception e) {
			log.error("액세스 토큰이 유효하지 않습니다.");
			return Optional.empty();
		}
	}

	/**
	 * RefreshToken DB 저장(업데이트)
	 */

	@Override
	public void updateRefreshToken(String email, String refreshToken) {
		CeoInfo ceoInfo = ceoRepository.findByEmail(email)
			.orElseThrow(() -> new NoSuchElementException("일치하는 회원이 없습니다."));

		ceoInfo.updateRefresh(refreshToken);
		ceoRepository.save(ceoInfo);
	}

	@Override
	public boolean isTokenValid(String token) {
		try {
			JWT.require(jwtTokenizer.getAlgorithm()).build().verify(token);
			return true;
		} catch (Exception e) {
			log.error("유효하지 않은 토큰입니다. {}", e.getMessage());
			return false;
		}
	}

}
