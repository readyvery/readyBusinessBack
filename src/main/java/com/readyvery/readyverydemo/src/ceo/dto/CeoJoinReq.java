package com.readyvery.readyverydemo.src.ceo.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CeoJoinReq {
	private String email;
	private String password;
	private String name;
	private String phone;
}
