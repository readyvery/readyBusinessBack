package com.readyvery.readyverydemo.src.user;

import com.readyvery.readyverydemo.src.user.dto.UserAuthRes;

public interface UserService {
	// 기존 메서드들...

	UserAuthRes getUserAuthByEmail(String email);
}
