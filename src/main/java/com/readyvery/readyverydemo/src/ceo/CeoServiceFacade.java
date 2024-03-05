package com.readyvery.readyverydemo.src.ceo;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.readyvery.readyverydemo.domain.CeoInfo;
import com.readyvery.readyverydemo.domain.Role;
import com.readyvery.readyverydemo.domain.repository.CeoRepository;
import com.readyvery.readyverydemo.global.exception.BusinessLogicException;
import com.readyvery.readyverydemo.global.exception.ExceptionCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CeoServiceFacade {
	private final CeoRepository ceoRepository;
	private final PasswordEncoder passwordEncoder;

	public boolean isExistPhone(String phoneNumber) {
		return ceoRepository.existsByPhone(phoneNumber);
	}

	public void updatePassword(CeoInfo ceoInfo, String password) {
		ceoInfo.updatePassword(password, passwordEncoder);
		ceoRepository.save(ceoInfo);
	}

	public void verifyCeoJoin(CeoInfo ceoInfo) {
		if (ceoRepository.existsByEmail(ceoInfo.getEmail())) {
			throw new BusinessLogicException(ExceptionCode.EMAIL_DUPLICATION);
		}

	}

	public CeoInfo getCeoInfo(Long id) {
		return ceoRepository.findById(id).orElseThrow(
			() -> new BusinessLogicException(ExceptionCode.USER_NOT_FOUND)
		);
	}

	public void changeRoleAndSave(Long userId, Role role) {
		CeoInfo ceoInfo = getCeoInfo(userId);
		ceoInfo.changeRole(role);
		ceoRepository.save(ceoInfo);
	}

	public CeoInfo getCeoInfoByPhone(String phoneNumber) {
		return ceoRepository.findByPhone(phoneNumber).orElseThrow(
			() -> new BusinessLogicException(ExceptionCode.USER_NOT_FOUND)
		);

	}

	public String findCeoEmailByPhone(String phoneNumber) {
		return ceoRepository.findEmailByPhone(phoneNumber).orElseThrow(
			() -> new BusinessLogicException(ExceptionCode.USER_NOT_FOUND)
		);
	}
}
