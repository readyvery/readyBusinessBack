package com.readyvery.readyverydemo.src.ceo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;

@Configuration
@Getter
public class CeoApiConfig {
	@Value("${jwt.refresh.cookie}")
	private String refreshCookie;
	@Value("${service.app.admin.key}")
	private String serviceAppAdminKey;

}
