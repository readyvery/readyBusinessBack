package com.readyvery.readyverydemo.src.user;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.readyvery.readyverydemo.domain.UserInfo;
import com.readyvery.readyverydemo.domain.repository.UserRepository;
import com.readyvery.readyverydemo.src.user.dto.UserAuthRes;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;

	// 기존 구현들...

	@Override
	public UserAuthRes getUserAuthByEmail(String email) {
		if (email == null || email.isEmpty()) {
			throw new IllegalArgumentException("Email must not be null or empty");
		}

		Optional<UserInfo> userInfoOptional = userRepository.findByEmail(email);

		if (userInfoOptional.isEmpty()) {
			throw new RuntimeException("User not found with email: " + email);
		}

		UserInfo userInfo = userInfoOptional.get();

		return UserAuthRes.builder()
			.id(userInfo.getId())
			.email(userInfo.getEmail())
			.name(userInfo.getNickName())
			.build();
	}
}



