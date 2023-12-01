package com.readyvery.readyverydemo.src.ceo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

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
import com.readyvery.readyverydemo.src.ceo.dto.CeoLogoutRes;
import com.readyvery.readyverydemo.src.ceo.dto.CeoMapper;
import com.readyvery.readyverydemo.src.ceo.dto.CeoRemoveRes;

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
	@Value("${service.app.admin.key}")
	private String serviceAppAdminKey;

	@Override
	public CeoAuthRes getCeoAuthByCustomUserDetails(CustomUserDetails userDetails) {
		verifyUserDetails(userDetails);
		return ceoMapper.ceoInfoToCeoAuthRes(userDetails);

	}

	@Override
	public CeoInfoRes getCeoInfoById(Long id) {
		CeoInfo ceoInfo = getCeoInfo(id);
		return ceoMapper.ceoInfoToCeoInfoRes(ceoInfo);
	}

	@Override
	public CeoLogoutRes removeRefreshTokenInDB(Long id, HttpServletResponse response) {
		CeoInfo user = getCeoInfo(id);
		user.updateRefresh(null); // Refresh Token을 null 또는 빈 문자열로 업데이트
		ceoRepository.save(user);
		invalidateRefreshTokenCookie(response); // 쿠키 무효화
		return CeoLogoutRes.builder()
			.success(true)
			.message("로그아웃 성공")
			.build();
	}

	@Override
	public CeoRemoveRes removeUser(Long id, HttpServletResponse response) throws IOException {
		CeoInfo user = getCeoInfo(id);
		requestToServer("KakaoAK " + serviceAppAdminKey,
			"target_id_type=user_id&target_id=" + user.getSocialId());
		user.updateRemoveCeoDate();
		user.updateRefresh(null); // Refresh Token을 null 또는 빈 문자열로 업데이트
		ceoRepository.save(user);
		invalidateRefreshTokenCookie(response); // 쿠키 무효화
		return CeoRemoveRes.builder()
			.message("회원 탈퇴가 완료되었습니다.")
			.success(true)
			.build();
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

	private void verifyUserDetails(CustomUserDetails userDetails) {
		if (userDetails == null) {
			throw new BusinessLogicException(ExceptionCode.USER_NOT_FOUND);
		}
	}

	private void requestToServer(String headerStr, String postData) throws IOException {
		URL url = new URL("https://kapi.kakao.com/v1/user/unlink");
		HttpURLConnection connectReq = null;

		try {
			connectReq = (HttpURLConnection)url.openConnection();
			connectReq.setRequestMethod("POST");
			connectReq.setDoOutput(true); // Enable writing to the connection output stream

			// Set headers
			connectReq.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			if (headerStr != null && !headerStr.isEmpty()) {
				connectReq.setRequestProperty("Authorization", headerStr);
			}

			// Write the post data to the request body
			try (OutputStream os = connectReq.getOutputStream()) {
				byte[] input = postData.getBytes(StandardCharsets.UTF_8);
				os.write(input, 0, input.length);
			}

			int responseCode = connectReq.getResponseCode();
			try (BufferedReader br = new BufferedReader(new InputStreamReader(
				responseCode == 200 ? connectReq.getInputStream() : connectReq.getErrorStream()))) {
				String inputLine;
				StringBuilder response = new StringBuilder();

				while ((inputLine = br.readLine()) != null) {
					response.append(inputLine);
				}
			}
		} finally {
			if (connectReq != null) {
				connectReq.disconnect();
			}
		}
	}

}
