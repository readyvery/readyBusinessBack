package com.readyvery.readyverydemo.src.ceo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CeoFindEmailReq {
	private String phoneNumber;
}
