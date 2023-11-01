package com.readyvery.readyverydemo.security.config;

import static org.springframework.security.config.Customizer.*;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SpringSecurityConfig {
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests((authz) -> authz
				//.requestMatchers("/api/v1/user").authenticated() // 해당 요청은 인증이 필요함
				.anyRequest().permitAll() // 위를 제외한 나머지는 모두 허용
			)
			//.formLogin(withDefaults()) // 기본 로그인 페이지 사용
			.httpBasic(withDefaults());
		return http.build();
	}

	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
		return (web) -> web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
	} // 정적 리소스 보안 필터 해제
}
