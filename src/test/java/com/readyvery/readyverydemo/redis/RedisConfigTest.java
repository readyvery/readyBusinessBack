package com.readyvery.readyverydemo.redis;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

@SpringBootTest
public class RedisConfigTest {

	@Autowired
	private RedisConnectionFactory redisConnectionFactory;

	@Test
	@DisplayName("RedisConnectionFactory 테스트")
	public void redisConnectionFactoryTest() {
		// RedisConnectionFactory가 null이 아닌지 확인
		assertThat(redisConnectionFactory).isNotNull();
		System.out.println("redisConnectionFactory = " + redisConnectionFactory);
		// RedisConnectionFactory의 클래스 타입이 LettuceConnectionFactory인지 확인
		assertThat(redisConnectionFactory).isInstanceOf(LettuceConnectionFactory.class);

	}
}
