package com.readyvery.readyverydemo.src.smsauthentication;

import org.springframework.stereotype.Service;

import com.readyvery.readyverydemo.config.SolApiConfig;
import com.readyvery.readyverydemo.domain.CeoInfo;
import com.readyvery.readyverydemo.global.exception.BusinessLogicException;
import com.readyvery.readyverydemo.global.exception.ExceptionCode;
import com.readyvery.readyverydemo.src.ceo.CeoServiceFacade;
import com.readyvery.readyverydemo.src.smsauthentication.dto.SmsSendFindEmailReq;
import com.readyvery.readyverydemo.src.smsauthentication.dto.SmsSendFindEmailRes;
import com.readyvery.readyverydemo.src.smsauthentication.dto.SmsSendRes;
import com.readyvery.readyverydemo.src.smsauthentication.dto.SmsVerifyReq;
import com.readyvery.readyverydemo.src.smsauthentication.dto.SmsVerifyRes;

import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SmsServiceImpl implements SmsService {

	private final SolApiConfig solApiConfig;
	private final MessageSendingService messageSendingService;
	private final VerificationService verificationService;
	private final CeoServiceFacade ceoServiceFacade;

	@Override
	public SmsSendRes sendSms(String phoneNumber) {
		// Message 패키지가 중복될 경우 net.nurigo.sdk.message.model.Message로 치환하여 주세요

		if (StringUtils.isEmpty(phoneNumber)) {
			throw new BusinessLogicException(ExceptionCode.INVALID_INPUT);
		}

		String code = verificationService.createVerificationCode(phoneNumber, false);
		String messageContent = "[Readyvery] 아래의 인증번호를 입력해주세요.\n인증번호 : " + code;
		boolean isMessageSent = messageSendingService.sendMessage(phoneNumber,
			solApiConfig.getPhoneNumber(), messageContent);

		if (isMessageSent) {
			return SmsSendRes.builder()
				.isSuccess(true)
				.smsMessage("인증번호가 발송되었습니다.")
				.build();
		} else {
			log.error("Message sending failed.");
			return SmsSendRes.builder()
				.isSuccess(false)
				.smsMessage("메시지 발송에 실패하였습니다.")
				.build();
		}
	}

	@Override
	public SmsVerifyRes verifySms(SmsVerifyReq smsVerifyReq) {

		if (StringUtils.isEmpty(smsVerifyReq.getPhoneNumber())) {
			throw new BusinessLogicException(ExceptionCode.INVALID_INPUT);
		}

		// Message 패키지가 중복될 경우 net.nurigo.sdk.message.model.Message로 치환하여 주세요
		boolean isValid = verificationService.verifyCode(smsVerifyReq.getPhoneNumber(), smsVerifyReq.getVerifyNumber());
		if (isValid) {

			return SmsVerifyRes.builder()
				.isSuccess(true)
				.smsMessage("인증에 성공하였습니다.")
				.build();
		} else {
			return SmsVerifyRes.builder()
				.isSuccess(false)
				.smsMessage("인증에 실패하였습니다.")
				.build();
		}

	}

	@Override
	public SmsSendFindEmailRes sendFindPasswordSms(SmsSendFindEmailReq smsSendFindEmailReq) {
		if (StringUtils.isEmpty(smsSendFindEmailReq.getEmail())) {
			throw new BusinessLogicException(ExceptionCode.INVALID_INPUT);
		}
		CeoInfo ceoInfo = ceoServiceFacade.getCeoInfoByEmail(smsSendFindEmailReq.getEmail());
		SmsSendRes sendSms = sendSms(ceoInfo.getPhone());

		return SmsSendFindEmailRes.builder()
			.isSuccess(sendSms.isSuccess())
			.message(sendSms.getSmsMessage())
			.build();

	}

}
