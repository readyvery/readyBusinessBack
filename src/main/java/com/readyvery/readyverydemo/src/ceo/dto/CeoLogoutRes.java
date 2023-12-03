package com.readyvery.readyverydemo.src.ceo.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CeoLogoutRes {
	private boolean success;
	private String message;
}
