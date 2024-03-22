package com.readyvery.readyverydemo.src.ceo.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CeoJoinRes {
	private boolean success;
	private String message;
}
