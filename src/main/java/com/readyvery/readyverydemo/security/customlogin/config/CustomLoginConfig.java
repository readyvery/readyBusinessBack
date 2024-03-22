package com.readyvery.readyverydemo.security.customlogin.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import lombok.Getter;

@Getter
@Configuration
public class CustomLoginConfig {

	public static final String DEFAULT_LOGIN_REQUEST_URL = "/api/v1/user/login"; // "/login"으로 오는 요청을 처리
	public static final String HTTP_METHOD = "POST"; // 로그인 HTTP 메소드는 POST
	public static final String CONTENT_TYPE = "application/json"; // JSON 타입의 데이터로 오는 로그인 요청만 처리
	public static final String USERNAME_KEY = "email"; // 회원 로그인 시 이메일 요청 JSON Key : "email"
	public static final String PASSWORD_KEY = "password"; // 회원 로그인 시 비밀번호 요청 JSon Key : "password"
	public static final AntPathRequestMatcher DEFAULT_LOGIN_PATH_REQUEST_MATCHER =
		new AntPathRequestMatcher(DEFAULT_LOGIN_REQUEST_URL, HTTP_METHOD); // "/login" + POST로 온 요청에 매칭된다.

}
