package com.readyvery.readyverydemo.solapi;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import net.nurigo.sdk.message.service.DefaultMessageService;

import com.readyvery.readyverydemo.config.SolApiConfig;
import com.readyvery.readyverydemo.domain.repository.CeoRepository;
import com.readyvery.readyverydemo.src.smsauthentication.SmsServiceImpl;
import com.readyvery.readyverydemo.src.smsauthentication.VerificationService;
import com.readyvery.readyverydemo.src.smsauthentication.dto.SmsVerifyReq;
import com.readyvery.readyverydemo.src.smsauthentication.dto.SmsVerifyRes;

@ExtendWith(MockitoExtension.class)
public class SmsServiceImplTest {

	@Mock
	private SolApiConfig solApiConfig;

	@Mock
	private CeoRepository ceoRepository;

	@Mock
	private DefaultMessageService messageService;

	@Mock
	private VerificationService verificationService;

	@InjectMocks
	private SmsServiceImpl smsService;

	@BeforeEach
	void setUp() {
		// 필요한 경우 초기화 코드 작성
	}

	// @Test
	// void testSendSms() {
	// 	// given
	// 	SmsSendReq request = new SmsSendReq("01064393547");
	// 	when(solApiConfig.getPhoneNumber()).thenReturn("01064393547");
	// 	when(verificationService.createVerificationCode(anyString())).thenReturn("123456");
	//
	// 	// when
	// 	SmsSendRes response = smsService.sendSms(1L, request);
	//
	// 	// then
	// 	assertTrue(response.isSuccess());
	// 	assertEquals("인증번호가 발송되었습니다.", response.getSmsMessage());
	// }
	//
	// @Test
	// void testVerifySms_Success() {
	// 	// given
	// 	SmsVerifyReq request = new SmsVerifyReq("01012345678", "123456");
	// 	when(verificationService.verifyCode(request.getPhoneNumber(), request.getVerifyNumber())).thenReturn(true);
	//
	// 	// when
	// 	SmsVerifyRes response = smsService.verifySms(1L, request);
	//
	// 	// then
	// 	assertTrue(response.isSuccess());
	// 	assertEquals("인증에 성공하였습니다.", response.getSmsMessage());
	// }

	@Test
	void testVerifySms_Failure() {
		// given
		SmsVerifyReq request = new SmsVerifyReq("01012345678", "123456");
		when(verificationService.verifyCode(request.getPhoneNumber(), request.getVerifyNumber())).thenReturn(false);

		// when
		SmsVerifyRes response = smsService.verifySms(1L, request);

		// then
		assertFalse(response.isSuccess());
		assertEquals("인증에 실패하였습니다.", response.getSmsMessage());
	}
}
