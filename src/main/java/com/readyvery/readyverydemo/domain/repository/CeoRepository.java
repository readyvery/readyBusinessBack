package com.readyvery.readyverydemo.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.readyvery.readyverydemo.domain.CeoInfo;
import com.readyvery.readyverydemo.domain.SocialType;

public interface CeoRepository extends JpaRepository<CeoInfo, Long> {

	Optional<CeoInfo> findByEmail(String email);

	Optional<CeoInfo> findByRefreshToken(String refreshToken);

	Optional<CeoInfo> findBySocialTypeAndSocialId(SocialType socialType, String socialId);

	boolean existsByEmail(String email);

	boolean existsByPhone(String phoneNumber);

	@Query("SELECT c.email FROM CeoInfo c WHERE c.phone = ?1")
	Optional<String> findEmailByPhone(String phoneNumber);

	Optional<CeoInfo> findByPhone(String phoneNumber);
}
