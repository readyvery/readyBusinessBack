package com.readyvery.readyverydemo.redis;

import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class RedisConnectionTest {

	// @Autowired
	// private StringRedisTemplate redisTemplate;
	//
	// @Test
	// @DisplayName("Redis 연결 테스트")
	// public void testRedisConnection() {
	// 	// 테스트 키-값 쌍 설정
	// 	String key = "testKey";
	// 	String value = "testValue";
	//
	// 	// Redis에 데이터 쓰기
	// 	redisTemplate.opsForValue().set(key, value);
	//
	// 	// Redis에서 데이터 읽기
	// 	String fetchedValue = redisTemplate.opsForValue().get(key);
	//
	// 	// 값 검증
	// 	assertThat(fetchedValue).isEqualTo(value);
	// }
}
