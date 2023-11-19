package com.readyvery.readyverydemo.src.ceo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.readyvery.readyverydemo.domain.CeoInfo;
import com.readyvery.readyverydemo.domain.repository.CeoRepository;
import com.readyvery.readyverydemo.global.exception.BusinessLogicException;
import com.readyvery.readyverydemo.global.exception.ExceptionCode;
import com.readyvery.readyverydemo.security.jwt.dto.CustomUserDetails;
import com.readyvery.readyverydemo.src.ceo.dto.CeoAuthRes;
import com.readyvery.readyverydemo.src.ceo.dto.CeoInfoRes;
import com.readyvery.readyverydemo.src.ceo.dto.CeoMapper;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class CeoServiceImpl implements CeoService {

	private final CeoRepository ceoRepository;
	private final CeoMapper ceoMapper;
	@Value("${jwt.refresh.cookie}")
	private String refreshCookie;

	@Override
	public CeoAuthRes getCeoAuthByCustomUserDetails(CustomUserDetails userDetails) {

		return ceoMapper.ceoInfoToCeoAuthRes(userDetails);

	}

	@Override
	public CeoInfoRes getCeoInfoById(Long id) {
		CeoInfo ceoInfo = getCeoInfo(id);
		return ceoMapper.ceoInfoToCeoInfoRes(ceoInfo);
	}

	@Override
	public void removeRefreshTokenInDB(Long id, HttpServletResponse response) {
		CeoInfo user = getCeoInfo(id);
		user.updateRefresh(null); // Refresh Token을 null 또는 빈 문자열로 업데이트
		ceoRepository.save(user);
		invalidateRefreshTokenCookie(response); // 쿠키 무효화
	}

	/**
	 * 로그아웃
	 * @param response
	 */
	private void invalidateRefreshTokenCookie(HttpServletResponse response) {
		Cookie refreshTokenCookie = new Cookie(refreshCookie, null); // 쿠키 이름을 동일하게 설정
		refreshTokenCookie.setHttpOnly(true);
		refreshTokenCookie.setPath("/api/v1/refresh/token"); // 기존과 동일한 경로 설정
		refreshTokenCookie.setMaxAge(0); // 만료 시간을 0으로 설정하여 즉시 만료
		response.addCookie(refreshTokenCookie);
	}

	private CeoInfo getCeoInfo(Long id) {
		return ceoRepository.findById(id).orElseThrow(
			() -> new BusinessLogicException(ExceptionCode.USER_NOT_FOUND)
		);
	}

}
