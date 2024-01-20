package com.readyvery.readyverydemo.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {

	GUEST("ROLE_GUEST"), USER("ROLE_USER"), CEO("ROLE_CEO"), ADMIN("ROLE_ADMIN");

	private final String key;
}

