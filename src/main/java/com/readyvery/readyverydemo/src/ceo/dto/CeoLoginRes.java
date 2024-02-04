package com.readyvery.readyverydemo.src.ceo.dto;

import com.readyvery.readyverydemo.domain.Role;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CeoLoginRes {
	private boolean success;
	private String message;
	private String accessToken;
	private String refreshToken;
	private Role role;
}
