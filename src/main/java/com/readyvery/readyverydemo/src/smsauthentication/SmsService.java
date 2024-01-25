package com.readyvery.readyverydemo.src.smsauthentication;

import com.readyvery.readyverydemo.src.smsauthentication.dto.SmsSendReq;
import com.readyvery.readyverydemo.src.smsauthentication.dto.SmsSendRes;
import com.readyvery.readyverydemo.src.smsauthentication.dto.SmsVerifyReq;
import com.readyvery.readyverydemo.src.smsauthentication.dto.SmsVerifyRes;

public interface SmsService {

	SmsSendRes sendSms(Long userId, SmsSendReq smsSendReq);

	SmsVerifyRes verifySms(Long userId, SmsVerifyReq smsVerifyReq);
}
