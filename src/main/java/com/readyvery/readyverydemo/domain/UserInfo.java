package com.readyvery.readyverydemo.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Builder
@Table(name = "USERS")
@AllArgsConstructor
@Slf4j
public class UserInfo extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id")
	private Long id;

	@Column(nullable = false)
	private String email; // 이메일

	@Column(nullable = false)
	private String nickName; // 닉네임


	private String imageUrl; // 프로필 이미지
	private int age; // 나이

	@Enumerated(EnumType.STRING)
	private Role role;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private SocialType socialType; // KAKAO, NAVER, GOOGLE

	@Column(nullable = false)
	private String socialId; // 로그인한 소셜 타입의 식별자 값 (일반 로그인인 경우 null)

	private String refreshToken; // 리프레시 토큰

	public void updateRefresh(String updateRefreshToken) {
		this.refreshToken = updateRefreshToken;
	}
}
