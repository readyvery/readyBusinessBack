package com.readyvery.readyverydemo.redis;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.readyvery.readyverydemo.domain.repository.CeoRepository;
import com.readyvery.readyverydemo.redis.dao.RefreshToken;
import com.readyvery.readyverydemo.redis.repository.RefreshTokenRepository;

@SpringBootTest
public class RefreshTokenRepositoryTest {

	@Autowired
	private RefreshTokenRepository refreshTokenRepository;

	@Autowired
	private CeoRepository ceoRepository;

	private final String testId = "1223v@naver.com";
	private final String testRefreshToken = "testRefreshToken1";
	private final String testAccessToken = "testAccessToken1";

	@BeforeEach
	public void setup() {
		RefreshToken token = new RefreshToken(testId, testRefreshToken);
		refreshTokenRepository.save(token);
	}

	// @AfterEach
	// public void cleanup() {
	// 	refreshTokenRepository.deleteById(testId);
	// }

	@Test
	public void testSaveAndFindById() {
		// ID로 조회

		refreshTokenRepository.findByRefreshToken(testRefreshToken)
			.map(RefreshToken::getId) // RefreshToken 객체에서 ID를 추출합니다.
			.flatMap(ceoRepository::findByEmail) // 추출된 ID를 이용하여 CEO를 조회합니다.
			.ifPresent(user -> {
				String userEmail = user.getEmail();
				assertThat(userEmail).isEqualTo(testId);
				System.out.println("testId = " + testId);
				System.out.println("userEmail = " + userEmail);
			});

	}

}

