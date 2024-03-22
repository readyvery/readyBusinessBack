package com.readyvery.readyverydemo.redis.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.readyvery.readyverydemo.redis.dao.RefreshToken;

@Repository
public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {

	Optional<RefreshToken> findByRefreshToken(String refreshToken);

}
