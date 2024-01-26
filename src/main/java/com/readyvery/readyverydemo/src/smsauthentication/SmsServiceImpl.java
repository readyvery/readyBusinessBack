package com.readyvery.readyverydemo.src.smsauthentication;

import org.springframework.stereotype.Service;

import com.readyvery.readyverydemo.config.SolApiConfig;
import com.readyvery.readyverydemo.domain.Role;
import com.readyvery.readyverydemo.global.exception.BusinessLogicException;
import com.readyvery.readyverydemo.global.exception.ExceptionCode;
import com.readyvery.readyverydemo.src.ceo.CeoService;
import com.readyvery.readyverydemo.src.smsauthentication.dto.SmsSendReq;
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
	private final CeoService ceoServiceImpl;

	@Override
	public SmsSendRes sendSms(Long userId, SmsSendReq smsSendReq) {
		// Message 패키지가 중복될 경우 net.nurigo.sdk.message.model.Message로 치환하여 주세요
		if (StringUtils.isEmpty(smsSendReq.getPhoneNumber())) {
			throw new BusinessLogicException(ExceptionCode.INVALID_INPUT);
		}

		String code = verificationService.createVerificationCode(smsSendReq.getPhoneNumber());

		ceoServiceImpl.insertPhoneNum(userId, smsSendReq.getPhoneNumber());

		String messageContent = "[Readyvery] 아래의 인증번호를 입력해주세요.\n인증번호 : " + code;
		boolean isMessageSent = messageSendingService.sendMessage(smsSendReq.getPhoneNumber(),
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
	public SmsVerifyRes verifySms(Long userId, SmsVerifyReq smsVerifyReq) {
		// Message 패키지가 중복될 경우 net.nurigo.sdk.message.model.Message로 치환하여 주세요
		boolean isValid = verificationService.verifyCode(smsVerifyReq.getPhoneNumber(), smsVerifyReq.getVerifyNumber());
		if (isValid) {
			ceoServiceImpl.changeRoleAndSave(userId, Role.USER);
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

}
