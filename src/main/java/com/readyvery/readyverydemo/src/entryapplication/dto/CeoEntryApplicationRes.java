package com.readyvery.readyverydemo.src.entryapplication.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CeoEntryApplicationRes {
	private boolean success;
	private String message;

}
