package com.readyvery.readyverydemo.src.ceo.dto;

import com.readyvery.readyverydemo.domain.Role;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CeoAuthRes {
	private Long id;
	private String email;
	private boolean auth;
	private Role role;
}
