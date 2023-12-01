package com.readyvery.readyverydemo.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.readyvery.readyverydemo.domain.CeoInfo;
import com.readyvery.readyverydemo.domain.SocialType;

public interface CeoRepository extends JpaRepository<CeoInfo, Long> {
	Optional<CeoInfo> findTop1ById(Long id);

	Optional<CeoInfo> findByEmail(String email);

	Optional<CeoInfo> findByRefreshToken(String refreshToken);

	Optional<CeoInfo> findBySocialTypeAndSocialId(SocialType socialType, String socialId);
}
