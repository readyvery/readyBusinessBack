package com.readyvery.readyverydemo.redis;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

@SpringBootTest
public class RedisConnectionTest {

	@Autowired
	private StringRedisTemplate redisTemplate;

	@Test
	@DisplayName("Redis 연결 테스트")
	public void testRedisConnection() {
		// 테스트 키-값 쌍 설정
		String key = "testKey";
		String value = "testValue";

		// Redis에 데이터 쓰기
		redisTemplate.opsForValue().set(key, value);

		// Redis에서 데이터 읽기
		String fetchedValue = redisTemplate.opsForValue().get(key);

		// 값 검증
		assertThat(fetchedValue).isEqualTo(value);
	}
}
