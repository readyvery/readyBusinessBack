package com.readyvery.readyverydemo.src.ceo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CeoDuplicateCheckReq {

	private String email;
}
