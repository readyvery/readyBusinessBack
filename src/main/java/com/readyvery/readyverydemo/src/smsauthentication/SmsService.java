package com.readyvery.readyverydemo.src.smsauthentication;

import com.readyvery.readyverydemo.src.smsauthentication.dto.SmsSendFindEmailReq;
import com.readyvery.readyverydemo.src.smsauthentication.dto.SmsSendFindEmailRes;
import com.readyvery.readyverydemo.src.smsauthentication.dto.SmsSendRes;
import com.readyvery.readyverydemo.src.smsauthentication.dto.SmsVerifyReq;
import com.readyvery.readyverydemo.src.smsauthentication.dto.SmsVerifyRes;

public interface SmsService {

	SmsSendRes sendSms(String phoneNumber);

	SmsVerifyRes verifySms(SmsVerifyReq smsVerifyReq);

	SmsSendFindEmailRes sendFindPasswordSms(SmsSendFindEmailReq smsSendFindEmailReq);
}
