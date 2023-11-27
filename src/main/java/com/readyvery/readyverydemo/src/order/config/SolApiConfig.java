package com.readyvery.readyverydemo.src.order.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;

@Getter
@Configuration
public class SolApiConfig {
	public static final String SOLAPI_URL = "https://api.solapi.com";

	@Value("${solapi.api_key}")
	private String apiKey;

	@Value("${solapi.secret_key}")
	private String apiSecret;

	@Value("${solapi.templete.cancel}")
	private String templeteCancel;

	@Value("${solapi.templete.pickup}")
	private String templetePickup;

	@Value("${solapi.templete.order}")
	private String templeteOrder;

	@Value("${solapi.kakao.pfid}")
	private String kakaoPfid;
}