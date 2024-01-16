package com.readyvery.readyverydemo.redis;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.readyvery.readyverydemo.redis.dao.RefreshToken;
import com.readyvery.readyverydemo.redis.repository.RefreshTokenRepository;

@SpringBootTest
public class RefreshTokenRepositoryTest {

	@Autowired
	private RefreshTokenRepository refreshTokenRepository;

	private final String testRefreshToken = "testRefreshToken1";
	private final String testAccessToken = "testAccessToken1";

	@BeforeEach
	public void setup() {
		RefreshToken token = new RefreshToken(UUID.randomUUID().toString(), testRefreshToken, testAccessToken);
		refreshTokenRepository.save(token);
	}

	// @AfterEach
	// public void cleanup() {
	// 	refreshTokenRepository.deleteById(testId);
	// }

	@Test
	public void testSaveAndFindById() {
		// ID로 조회
		Optional<RefreshToken> found = refreshTokenRepository.findByAccessToken(testAccessToken);
		assertTrue(found.isPresent());
		assertEquals(testRefreshToken, found.get().getRefreshToken());
		assertEquals(testAccessToken, found.get().getAccessToken());
		System.out.println("testAccessToken = " + testAccessToken);
		System.out.println("found.get().getAccessToken() = " + found.get().getAccessToken());
	}

	@Test
	public void testDelete() {
		// 삭제 테스트
		refreshTokenRepository.deleteByAccessToken(testAccessToken);
		Optional<RefreshToken> found = refreshTokenRepository.findByAccessToken(testAccessToken);
		assertFalse(found.isPresent());
	}
}
