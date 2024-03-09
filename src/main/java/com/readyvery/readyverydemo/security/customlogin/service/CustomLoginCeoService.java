package com.readyvery.readyverydemo.security.customlogin.service;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.readyvery.readyverydemo.domain.CeoInfo;
import com.readyvery.readyverydemo.src.ceo.CeoServiceFacade;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomLoginCeoService implements UserDetailsService {

	private final CeoServiceFacade ceoServiceFacade;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		CeoInfo ceoInfo = ceoServiceFacade.getCeoInfoByEmail(email);

		return User.builder()
			.username(ceoInfo.getEmail())
			.password(ceoInfo.getPassword())
			.roles(ceoInfo.getRole().name())
			.build();
	}

}
