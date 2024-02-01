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
import com.readyvery.readyverydemo.domain.Role;
import com.readyvery.readyverydemo.domain.repository.CeoRepository;
import com.readyvery.readyverydemo.src.ceo.CeoService;
import com.readyvery.readyverydemo.src.smsauthentication.MessageSendingService;
import com.readyvery.readyverydemo.src.smsauthentication.SmsServiceImpl;
import com.readyvery.readyverydemo.src.smsauthentication.VerificationService;
import com.readyvery.readyverydemo.src.smsauthentication.dto.SmsSendReq;
import com.readyvery.readyverydemo.src.smsauthentication.dto.SmsSendRes;
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

	@Mock
	private CeoService ceoServiceImpl;

	@InjectMocks
	private SmsServiceImpl smsService;

	@Mock
	private MessageSendingService messageSendingService; // MessageSendingService에 대한 모킹 추가

	@BeforeEach
	void setUp() {
		// 필요한 경우 초기화 코드 작성
	}

	@Test
	void testSendSms() {
		// given
		SmsSendReq request = new SmsSendReq("01064393547");
		when(solApiConfig.getPhoneNumber()).thenReturn("01064393547");
		when(verificationService.createVerificationCode(anyString(), false)).thenReturn("123456");
		when(messageSendingService.sendMessage(request.getPhoneNumber(), solApiConfig.getPhoneNumber(),
			"[Readyvery] 아래의 인증번호를 입력해주세요.\n인증번호 : 123456")).thenReturn(
			true); // sendMessage의 결과를 모킹

		// when
		SmsSendRes response = smsService.sendSms(request);

		// then
		assertTrue(response.isSuccess());
		assertEquals("인증번호가 발송되었습니다.", response.getSmsMessage());
	}

	@Test
	void testVerifySms_Success() {
		// given
		SmsVerifyReq request = new SmsVerifyReq("01012345678", "123456");
		when(verificationService.verifyCode(request.getPhoneNumber(), request.getVerifyNumber())).thenReturn(true);
		doNothing().when(ceoServiceImpl).changeRoleAndSave(1L, Role.USER);

		// when
		SmsVerifyRes response = smsService.verifySms(request);

		// then
		assertTrue(response.isSuccess());
		assertEquals("인증에 성공하였습니다.", response.getSmsMessage());
	}

	@Test
	void testVerifySms_Failure() {
		// given
		SmsVerifyReq request = new SmsVerifyReq("01012345678", "123456");
		when(verificationService.verifyCode(request.getPhoneNumber(), request.getVerifyNumber())).thenReturn(false);

		// when
		SmsVerifyRes response = smsService.verifySms(request);

		// then
		assertFalse(response.isSuccess());
		assertEquals("인증에 실패하였습니다.", response.getSmsMessage());
	}

	@Test
	void testVerifyNumber() {
		// given
		String phoneNumber = "01012345678";
		when(verificationService.verifyNumber(phoneNumber)).thenReturn(true);

		// when
		boolean result = verificationService.verifyNumber(phoneNumber);

		// then
		assertTrue(result);
	}
}
