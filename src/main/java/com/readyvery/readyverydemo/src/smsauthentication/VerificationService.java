package com.readyvery.readyverydemo.src.smsauthentication;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VerificationService {

	private final RedisTemplate<String, String> redisTemplate;

	public String createVerificationCode(String phoneNumber) {
		String code = UUID.randomUUID().toString().substring(0, 6);
		redisTemplate.opsForValue().set(phoneNumber, code, 5, TimeUnit.MINUTES);
		return code;
	}

	public boolean verifyCode(String phoneNumber, String code) {
		String storedCode = redisTemplate.opsForValue().get(phoneNumber);
		return storedCode != null && storedCode.equals(code);
	}
}
