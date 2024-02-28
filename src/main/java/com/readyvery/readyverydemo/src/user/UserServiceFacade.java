package com.readyvery.readyverydemo.src.user;

import static com.readyvery.readyverydemo.global.exception.ExceptionCode.*;

import org.springframework.stereotype.Service;

import com.readyvery.readyverydemo.domain.UserInfo;
import com.readyvery.readyverydemo.domain.repository.UserRepository;
import com.readyvery.readyverydemo.global.exception.BusinessLogicException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceFacade {
	private final UserRepository userRepository;

	public UserInfo getUserInfo(Long id) {
		return userRepository.findById(id).orElseThrow(
			() -> new BusinessLogicException(USER_NOT_FOUND)
		);
	}

	public void saveUser(UserInfo userInfo) {
		userRepository.save(userInfo);
	}
}
