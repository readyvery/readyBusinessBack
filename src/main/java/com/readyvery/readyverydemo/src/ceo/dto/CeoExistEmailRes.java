package com.readyvery.readyverydemo.src.ceo.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CeoExistEmailRes {

	private boolean isSuccess;
	private String email;
	private String message;

}
