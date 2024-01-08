package com.readyvery.readyverydemo.security.customlogin.filter;

import static com.readyvery.readyverydemo.security.customlogin.config.CustomLoginConfig.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.util.StreamUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CustomJsonCeonamePasswordAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

	private final ObjectMapper objectMapper;

	public CustomJsonCeonamePasswordAuthenticationFilter(ObjectMapper objectMapper) {
		super(DEFAULT_LOGIN_REQUEST_URL);
		this.objectMapper = objectMapper;
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws
		AuthenticationException,
		IOException {
		if (request.getContentType() == null || !request.getContentType().equals(CONTENT_TYPE)) {
			throw new AuthenticationServiceException(
				"Authentication Content-Type not supported: " + request.getContentType());
		}
		String messageBody = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
		Map<String, String> usernamePasswordMap = objectMapper.readValue(messageBody, Map.class);

		String email = usernamePasswordMap.get(USERNAME_KEY);
		String password = usernamePasswordMap.get(PASSWORD_KEY);

		UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(email, password);
		return this.getAuthenticationManager().authenticate(authRequest);
	}

}
