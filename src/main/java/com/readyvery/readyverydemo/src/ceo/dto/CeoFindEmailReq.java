package com.readyvery.readyverydemo.src.ceo.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CeoFindEmailReq {
	private String phoneNumber;
}
