package com.readyvery.readyverydemo.src.ceo;

import java.io.IOException;

import com.readyvery.readyverydemo.domain.CeoInfo;
import com.readyvery.readyverydemo.domain.Role;
import com.readyvery.readyverydemo.security.jwt.dto.CustomUserDetails;
import com.readyvery.readyverydemo.src.ceo.dto.CeoAuthRes;
import com.readyvery.readyverydemo.src.ceo.dto.CeoDuplicateCheckReq;
import com.readyvery.readyverydemo.src.ceo.dto.CeoDuplicateCheckRes;
import com.readyvery.readyverydemo.src.ceo.dto.CeoInfoRes;
import com.readyvery.readyverydemo.src.ceo.dto.CeoJoinReq;
import com.readyvery.readyverydemo.src.ceo.dto.CeoJoinRes;
import com.readyvery.readyverydemo.src.ceo.dto.CeoLogoutRes;
import com.readyvery.readyverydemo.src.ceo.dto.CeoRemoveRes;

import jakarta.servlet.http.HttpServletResponse;

public interface CeoService {

	CeoAuthRes getCeoAuthByCustomUserDetails(CustomUserDetails userDetails);

	CeoInfoRes getCeoInfoById(Long id);

	CeoLogoutRes removeRefreshTokenInDB(CustomUserDetails userDetails, HttpServletResponse response);

	CeoRemoveRes removeUser(CustomUserDetails userDetails, HttpServletResponse response) throws IOException;

	CeoJoinRes join(CeoJoinReq ceoJoinReq);

	CeoInfo getCeoInfo(Long id);

	void changeRoleAndSave(Long userId, Role role);

	void insertPhoneNum(Long userId, String phoneNum);

	CeoDuplicateCheckRes emailDuplicateCheck(CeoDuplicateCheckReq ceoDuplicateCheckReq);

}
