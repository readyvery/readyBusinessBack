package com.readyvery.readyverydemo.src.entryapplication;

import java.io.IOException;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.readyvery.readyverydemo.security.jwt.dto.CustomUserDetails;
import com.readyvery.readyverydemo.src.entryapplication.dto.CeoEntryApplicationReq;
import com.readyvery.readyverydemo.src.entryapplication.dto.CeoEntryApplicationRes;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
public class CeoEntryApplicationController {

	private final CeoEntryApplicationService ceoEntryApplicationServiceImpl;

	/**
	 * 사용자 메타 정보 작성
	 */
	@Operation(summary = "사용자 메타 정보 작성 기능", description = "사용자 메타 정보를 작성합니다.", tags = {"유저 정보"})
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK")
	})
	@PostMapping(value = "/ceo/entry", consumes = "multipart/form-data")
	public CeoEntryApplicationRes entryApplication(@AuthenticationPrincipal CustomUserDetails userDetails,
		@ModelAttribute CeoEntryApplicationReq ceoEntryApplicationReq) throws
		IOException {
		return ceoEntryApplicationServiceImpl.entryApplication(userDetails.getId(), ceoEntryApplicationReq);

	}

}
