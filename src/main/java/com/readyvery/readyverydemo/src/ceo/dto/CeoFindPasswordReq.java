package com.readyvery.readyverydemo.src.ceo.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CeoFindPasswordReq {
	private String phoneNumber;
	private String password;
	private String confirmPassword;
}
