package com.readyvery.readyverydemo.src.ceo.dto;

import org.springframework.web.multipart.MultipartFile;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CeoMetaInfoReq {

	private String storeName;
	private String storeAddress;
	private String registrationNumber;
	private MultipartFile businessLicense;
	private MultipartFile businessReport;
	private MultipartFile identityCard;
	private MultipartFile bankAccount;
}
