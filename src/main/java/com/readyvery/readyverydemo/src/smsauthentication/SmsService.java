package com.readyvery.readyverydemo.src.smsauthentication;

import com.readyvery.readyverydemo.src.smsauthentication.dto.SmsSendFindEmailReq;
import com.readyvery.readyverydemo.src.smsauthentication.dto.SmsSendRes;
import com.readyvery.readyverydemo.src.smsauthentication.dto.SmsVerifyReq;
import com.readyvery.readyverydemo.src.smsauthentication.dto.SmsVerifyRes;

public interface SmsService {

	SmsSendRes sendSmsFromSolApi(String phoneNumber, String code);

	SmsVerifyRes verifySmsToFindPassword(SmsVerifyReq smsVerifyReq);

	SmsSendRes sendSms(String phoneNumber);

	SmsVerifyRes verifySms(SmsVerifyReq smsVerifyReq);

	SmsSendRes sendFindPasswordSms(SmsSendFindEmailReq smsSendFindEmailReq);


}
