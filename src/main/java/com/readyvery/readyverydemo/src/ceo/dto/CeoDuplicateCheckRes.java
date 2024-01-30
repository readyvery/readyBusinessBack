package com.readyvery.readyverydemo.src.ceo.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CeoDuplicateCheckRes {

	private boolean success;
	private String message;

}
