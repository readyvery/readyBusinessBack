package com.readyvery.readyverydemo.config;

import java.util.Arrays;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.readyvery.readyverydemo.domain.repository.CeoRepository;
import com.readyvery.readyverydemo.redis.repository.RefreshTokenRepository;
import com.readyvery.readyverydemo.security.customlogin.filter.CustomJsonCeonamePasswordAuthenticationFilter;
import com.readyvery.readyverydemo.security.customlogin.handler.LoginFailureHandler;
import com.readyvery.readyverydemo.security.customlogin.handler.LoginSuccessHandler;
import com.readyvery.readyverydemo.security.customlogin.service.CustomLoginCeoService;
import com.readyvery.readyverydemo.security.exception.CustomAuthenticationEntryPoint;
import com.readyvery.readyverydemo.security.jwt.filter.JwtAuthenticationProcessingFilter;
import com.readyvery.readyverydemo.security.jwt.service.JwtService;
import com.readyvery.readyverydemo.security.oauth2.handler.OAuth2LoginFailureHandler;
import com.readyvery.readyverydemo.security.oauth2.handler.OAuth2LoginSuccessHandler;
import com.readyvery.readyverydemo.security.oauth2.service.CustomOAuth2UserService;
import com.readyvery.readyverydemo.src.ceo.CeoServiceFacade;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SpringSecurityConfig {

	private final JwtService jwtService;
	private final CeoRepository ceoRepository;
	private final ObjectMapper objectMapper;
	private final CustomLoginCeoService customLoginCeoService;
	private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
	private final OAuth2LoginFailureHandler oAuth2LoginFailureHandler;
	private final CustomOAuth2UserService customOAuth2UserService;
	private final RefreshTokenRepository refreshTokenRepository;
	private final CeoServiceFacade ceoServiceFacade;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			// [PART 1]
			.cors(cors -> cors.configurationSource(corsConfigurationSource()))
			.csrf(AbstractHttpConfigurer::disable)
			.sessionManagement((sessionManagement) ->
				sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			)
			.formLogin(AbstractHttpConfigurer::disable)
			.httpBasic(AbstractHttpConfigurer::disable)

			// [PART 2]
			//== URL별 권한 관리 옵션 ==//
			.authorizeHttpRequests((authz) -> authz
				.requestMatchers("/v1/user/join",
					"/v1/user/login",
					"/v1/user/duplicate/check",
					"/v1/ceo/find/**"
				).anonymous() // 로그인되지 않은 사용자만 접근 가능
				.requestMatchers(
					"/v1/jwt-test",
					"/oauth2/**",
					"/login",
					"/v1/auth",
					"/v1/sms/**"
				).permitAll() // 해당 요청은 모두 허용

				.requestMatchers("/swagger-ui/**", "/v1/api-docs/**", "/v3/api-docs/**",
					"/swagger-resources/**",
					"/webjars/**").permitAll()
				.anyRequest().authenticated() // 위를 제외한 나머지는 모두 인증이 필요
			)
			// [PART 3]
			//== 소셜 로그인 설정 ==//
			.oauth2Login(oauth2 -> oauth2
				.successHandler(oAuth2LoginSuccessHandler) // 동의하고 계속하기를 눌렀을 때 Handler 설정
				.failureHandler(oAuth2LoginFailureHandler) // 소셜 로그인 실패 시 핸들러 설정
				.userInfoEndpoint(userInfo -> userInfo
					.userService(customOAuth2UserService)))

			// Custom Exception Handling
			.exceptionHandling(exceptionHandling ->
				exceptionHandling.authenticationEntryPoint(customAuthenticationEntryPoint(new ObjectMapper()))
			);

		// [PART4]
		// 원래 스프링 시큐리티 필터 순서가 LogoutFilter 이후에 로그인 필터 동작
		// 따라서, LogoutFilter 이후에 우리가 만든 필터 동작하도록 설정
		// 순서 : LogoutFilter -> JwtAuthenticationProcessingFilter -> CustomJsonUsernamePasswordAuthenticationFilter
		http.addFilterAfter(customJsonCeonamePasswordAuthenticationFilter(), LogoutFilter.class);
		http.addFilterBefore(jwtAuthenticationProcessingFilter(), CustomJsonCeonamePasswordAuthenticationFilter.class);

		// http.addFilterBefore(jwtAuthenticationProcessingFilter(), LogoutFilter.class);as
		return http.build();
	}

	@Bean
	public CustomAuthenticationEntryPoint customAuthenticationEntryPoint(ObjectMapper objectMapper) {
		return new CustomAuthenticationEntryPoint(objectMapper);
	}

	@Bean
	public AuthenticationManager authenticationManager() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setPasswordEncoder(passwordEncoder());
		provider.setUserDetailsService(customLoginCeoService);
		return new ProviderManager(provider);
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	} // 패스워드 인코더

	@Bean
	public LoginSuccessHandler loginSuccessHandler() {
		return new LoginSuccessHandler(jwtService, refreshTokenRepository, ceoServiceFacade);
	}

	@Bean
	public LoginFailureHandler loginFailureHandler() {
		return new LoginFailureHandler(objectMapper);
	}

	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		// TODO: 이 부분은 나중에 삭제해야 됨
		//configuration.setAllowedMethods(Arrays.asList("*")); // 모든 HTTP 메서드 허용
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOriginPatterns(Arrays.asList("*")); // 변경된 설정
		configuration.setAllowedMethods(Arrays.asList("GET", "POST", "OPTIONS", "DELETE", "PUT", "PATCH"));
		configuration.setAllowedHeaders(Arrays.asList("*"));
		configuration.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	} // CORS 설정

	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
		return (web) -> web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
	} // 정적 리소스 보안 필터 해제

	@Bean
	public CustomJsonCeonamePasswordAuthenticationFilter customJsonCeonamePasswordAuthenticationFilter() {
		CustomJsonCeonamePasswordAuthenticationFilter customJsonCeonamePasswordAuthenticationFilter =
			new CustomJsonCeonamePasswordAuthenticationFilter(objectMapper);
		customJsonCeonamePasswordAuthenticationFilter.setAuthenticationManager(authenticationManager());
		customJsonCeonamePasswordAuthenticationFilter.setAuthenticationSuccessHandler(loginSuccessHandler());
		customJsonCeonamePasswordAuthenticationFilter.setAuthenticationFailureHandler(loginFailureHandler());
		return customJsonCeonamePasswordAuthenticationFilter;
	}

	@Bean
	public JwtAuthenticationProcessingFilter jwtAuthenticationProcessingFilter() {
		JwtAuthenticationProcessingFilter jwtAuthenticationFilter = new JwtAuthenticationProcessingFilter(jwtService,
			ceoRepository, refreshTokenRepository);
		return jwtAuthenticationFilter;
	}
}
