package com.readyvery.readyverydemo.src.smsauthentication;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.readyvery.readyverydemo.src.smsauthentication.dto.SmsSendFindEmailReq;
import com.readyvery.readyverydemo.src.smsauthentication.dto.SmsSendFindEmailRes;
import com.readyvery.readyverydemo.src.smsauthentication.dto.SmsSendReq;
import com.readyvery.readyverydemo.src.smsauthentication.dto.SmsSendRes;
import com.readyvery.readyverydemo.src.smsauthentication.dto.SmsVerifyReq;
import com.readyvery.readyverydemo.src.smsauthentication.dto.SmsVerifyRes;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
public class SmsController {

	private final SmsService smsServiceImpl;

	@Operation(summary = "SMS 인증 번호 발급 기능", description = "SMS 인증 번호를 발급합니다.", tags = {"SMS 인증"})
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK"),
	})
	@PostMapping("/sms/send")
	public SmsSendRes sendSms(
		@RequestBody SmsSendReq smsSendReq) {
		return smsServiceImpl.sendSms(smsSendReq.getPhoneNumber());
	}

	@Operation(summary = "SMS 인증 번호 검증 기능", description = "SMS 인증 번호를 검증합니다.", tags = {"SMS 인증"})
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK"),
	})
	@PostMapping("/sms/verify")
	public SmsVerifyRes verifySms(
		@RequestBody SmsVerifyReq smsVerifyReq) {
		return smsServiceImpl.verifySms(smsVerifyReq);
	}

	@Operation(summary = "아이디로 SMS 인증 번호 발급 기능", description = "아이디로 SMS 인증 번호를 발급합니다.", tags = {"SMS 인증"})
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "OK"),
	})
	@PostMapping("/sms/send/find-email")
	public SmsSendFindEmailRes sendFindPasswordSms(
		@RequestBody SmsSendFindEmailReq smsSendFindEmailReq) {
		return smsServiceImpl.sendFindPasswordSms(smsSendFindEmailReq);
	}

}
