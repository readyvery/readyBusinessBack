package com.readyvery.readyverydemo.src.smsauthentication;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.readyvery.readyverydemo.security.jwt.dto.CustomUserDetails;
import com.readyvery.readyverydemo.src.smsauthentication.dto.SmsSendReq;
import com.readyvery.readyverydemo.src.smsauthentication.dto.SmsSendRes;
import com.readyvery.readyverydemo.src.smsauthentication.dto.SmsVerifyReq;
import com.readyvery.readyverydemo.src.smsauthentication.dto.SmsVerifyRes;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class SmsController {

	private final SmsService smsServiceImpl;

	@PostMapping("/sms/send")
	public SmsSendRes sendSms(@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestBody SmsSendReq smsSendReq) {
		return smsServiceImpl.sendSms(userDetails.getId(), smsSendReq);
	}

	@PostMapping("/sms/verify")
	public SmsVerifyRes verifySms(@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestBody SmsVerifyReq smsVerifyReq) {
		return smsServiceImpl.verifySms(userDetails.getId(), smsVerifyReq);
	}

}
