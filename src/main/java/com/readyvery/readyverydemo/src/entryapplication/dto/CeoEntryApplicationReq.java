package com.readyvery.readyverydemo.src.entryapplication.dto;

import org.springframework.web.multipart.MultipartFile;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CeoEntryApplicationReq {
	private String storeName;
	private String storeAddress;
	private String registrationNumber;
	private MultipartFile businessLicense;
	private MultipartFile businessReport;
	private MultipartFile identityCard;
	private MultipartFile bankAccount;
}
