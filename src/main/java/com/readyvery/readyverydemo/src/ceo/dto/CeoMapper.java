package com.readyvery.readyverydemo.src.ceo.dto;

import org.springframework.stereotype.Component;

import com.readyvery.readyverydemo.domain.CeoInfo;
import com.readyvery.readyverydemo.domain.Role;
import com.readyvery.readyverydemo.security.jwt.dto.CustomUserDetails;

@Component
public class CeoMapper {

	public CeoAuthRes ceoInfoToCeoAuthRes(CustomUserDetails userDetails) {
		return CeoAuthRes.builder()
			.id(userDetails.getId())
			.email(userDetails.getEmail())
			.auth(userDetails.isEnabled())
			.role(userDetails.getRole())
			.build();
	}

	public CeoInfoRes ceoInfoToCeoInfoRes(CeoInfo ceoInfo) {
		return CeoInfoRes.builder()
			.name(ceoInfo.getNickName())
			.storeName(ceoInfo.getStore().getName())
			.subStoreName(ceoInfo.getStore().getSubName())
			.address(ceoInfo.getStore().getAddress())
			.phone(ceoInfo.getPhone())
			.openTime(ceoInfo.getStore().getTime())
			.account(ceoInfo.getStore().getAccount())
			.build();
	}

	public CeoInfo ceoJoinReqToCeoInfo(CeoJoinReq ceoJoinReq) {
		return CeoInfo.builder()
			.email(ceoJoinReq.getEmail())
			.password(ceoJoinReq.getPassword())
			.nickName(ceoJoinReq.getName())
			.phone(ceoJoinReq.getPhone())
			.role(Role.USER)
			.build();
	}
}
