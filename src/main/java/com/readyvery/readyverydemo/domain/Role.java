package com.readyvery.readyverydemo.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {

	GUEST("ROLE_GUEST"), USER("ROLE_USER"), REVIEW("ROLE_REVIEW"), REJECT("ROLE_REJECT"),
	READY("ROLE_READY"), CEO("ROLE_CEO"), ADMIN(
		"ROLE_ADMIN");

	private final String key;
}

