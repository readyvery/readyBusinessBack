package com.readyvery.readyverydemo.src.ceo.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CeoInfoRes {

	private String name;
	private String storeName;
	private String address;
	private String phone;
	private String openTime;
	private String account;

}
