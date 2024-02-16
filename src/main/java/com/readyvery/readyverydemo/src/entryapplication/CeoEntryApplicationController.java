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

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
public class CeoEntryApplicationController {

	private final CeoEntryApplicationService ceoEntryApplicationServiceImpl;

	@PostMapping(value = "/ceo/entry", consumes = "multipart/form-data")
	public CeoEntryApplicationRes entryApplication(@AuthenticationPrincipal CustomUserDetails userDetails,
		@ModelAttribute CeoEntryApplicationReq ceoEntryApplicationReq) throws
		IOException {
		return ceoEntryApplicationServiceImpl.entryApplication(userDetails.getId(), ceoEntryApplicationReq);

	}

}
