package com.readyvery.readyverydemo.redis.dao;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
@RedisHash(value = "refreshToken", timeToLive = 60 * 60 * 24 * 30)
public class RefreshToken {

	@Id
	private String id;

	private String refreshToken;

	// @Indexed
	// private String accessToken;

	public void update(String refreshToken) {
		this.refreshToken = refreshToken;

	}
}
