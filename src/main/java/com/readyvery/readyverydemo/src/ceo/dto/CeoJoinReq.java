package com.readyvery.readyverydemo.src.ceo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CeoJoinReq {
	private String email;
	private String password;
	private String confirmPassword;
	private String name;
	private String phone;

}
