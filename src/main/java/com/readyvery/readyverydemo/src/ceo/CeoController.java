package com.readyvery.readyverydemo.src.ceo;

import java.io.IOException;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.readyvery.readyverydemo.security.jwt.dto.CustomUserDetails;
import com.readyvery.readyverydemo.src.ceo.dto.CeoAuthRes;
import com.readyvery.readyverydemo.src.ceo.dto.CeoDuplicateCheckReq;
import com.readyvery.readyverydemo.src.ceo.dto.CeoDuplicateCheckRes;
import com.readyvery.readyverydemo.src.ceo.dto.CeoInfoRes;
import com.readyvery.readyverydemo.src.ceo.dto.CeoJoinReq;
import com.readyvery.readyverydemo.src.ceo.dto.CeoJoinRes;
import com.readyvery.readyverydemo.src.ceo.dto.CeoLogoutRes;
import com.readyvery.readyverydemo.src.ceo.dto.CeoRemoveRes;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
public class CeoController {

	private final CeoService ceoServiceImpl;

	/**
	 * 사용자 인증 체크
	 * 인증체크 후 사용자 정보를 반환합니다.
	 * DB의 조회 없이 반환
	 * @param userDetails
	 * @return
	 */
	@Operation(summary = "유저 인증 기능", description = "유저를 인증 합니다.", tags = {"유저 정보"})
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK"),
	})
	@GetMapping("/auth")
	public CeoAuthRes userAuth(@AuthenticationPrincipal CustomUserDetails userDetails) {
		// 서비스 계층을 호출하여 사용자 정보를 조회합니다.
		return ceoServiceImpl.getCeoAuthByCustomUserDetails(userDetails);
	}

	/**
	 * 사용자 정보 조회
	 */
	@Operation(summary = "사용자 정보 조회 기능", description = "사용자 정보를 조회합니다.", tags = {"유저 정보"})
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK")
	})
	@GetMapping("/user/info")
	public CeoInfoRes userInfo(@AuthenticationPrincipal CustomUserDetails userDetails) {
		return ceoServiceImpl.getCeoInfoById(userDetails.getId());
	}

	/**
	 * 사용자 정보 조회
	 * CustomUserDetails의 내부 구현체인 UserDetails를 사용하여도 사용자 정보를 조회가능
	 * 인증체크 후 사용자 정보를 반환
	 * @param userDetails
	 * @return
	 */
	@Operation(summary = "사용자 정보 조회 기능", description = "사용자 정보를 조회합니다.", tags = {"유저 정보"})
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK")
	})
	@GetMapping("/user/detail/info")
	public CustomUserDetails userDetail(@AuthenticationPrincipal CustomUserDetails userDetails) {
		return userDetails;
	}

	/**
	 * 사용자 로그아웃
	 */
	@Operation(summary = "카카오 유저 로그아웃 기능", description = "카카오 유저를 로그아웃합니다.", tags = {"OAuth2.0"})
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK")
	})
	@GetMapping("/user/logout")
	public CeoLogoutRes logout(@AuthenticationPrincipal CustomUserDetails userDetails, HttpServletResponse response) {

		return ceoServiceImpl.removeRefreshTokenInDB(userDetails, response);

	}

	/**
	 * Access 토큰 재발급
	 *
	 * @return
	 */
	@Operation(summary = "유저 refresh 재갱신 기능", description = "유저의 refresh 재갱신합니다.", tags = {"유저 정보"})
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK")
	})
	@GetMapping("/refresh/token")
	public boolean refreshEndpoint() {
		return true;
	}

	/**
	 * 회원 탈퇴
	 * @param userDetails
	 * @return
	 * @throws IOException
	 */
	@Operation(summary = "카카오 회원 탈퇴 기능", description = "주문 상태를 변경합니다.", tags = {"OAuth2.0"})
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK"),
	})
	@GetMapping("/user/remove")
	public CeoRemoveRes remove(@AuthenticationPrincipal CustomUserDetails userDetails,
		HttpServletResponse response) throws IOException {
		return ceoServiceImpl.removeUser(userDetails, response);
	}

	/**
	 * 회원 가입
	 */
	@Operation(summary = "자체 회원가입 기능", description = "사용자 자체 회원가입합니다.", tags = {"유저 정보"})
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK")
	})
	@PostMapping("/user/join")
	public CeoJoinRes join(@RequestBody CeoJoinReq ceoJoinReq) {
		return ceoServiceImpl.join(ceoJoinReq);
	}

	/**
	 * 이메일 중복 체크
	 */
	@Operation(summary = "이메일 검증 기능", description = "사용자의 이메일을 검증합니다.", tags = {"유저 정보"})
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK")
	})
	@PostMapping("/user/duplicate/check")
	public CeoDuplicateCheckRes emailDuplicateCheck(@RequestBody CeoDuplicateCheckReq ceoDuplicateCheckReq) {
		return ceoServiceImpl.emailDuplicateCheck(ceoDuplicateCheckReq);
	}

}
