package com.readyvery.readyverydemo.src.ceo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class CeoDuplicateCheckRes {

	private boolean success;
	private String message;

}
