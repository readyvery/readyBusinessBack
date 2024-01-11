package com.readyvery.readyverydemo.src.ceo.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CeoLoginRes {
	private boolean success;
	private String message;
	private String accessToken;
	private String refreshToken;
}
