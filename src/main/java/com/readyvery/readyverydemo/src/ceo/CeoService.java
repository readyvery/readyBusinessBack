package com.readyvery.readyverydemo.src.ceo;

import java.io.IOException;

import com.readyvery.readyverydemo.security.jwt.dto.CustomUserDetails;
import com.readyvery.readyverydemo.src.ceo.dto.CeoAuthRes;
import com.readyvery.readyverydemo.src.ceo.dto.CeoDuplicateCheckReq;
import com.readyvery.readyverydemo.src.ceo.dto.CeoDuplicateCheckRes;
import com.readyvery.readyverydemo.src.ceo.dto.CeoExistEmailRes;
import com.readyvery.readyverydemo.src.ceo.dto.CeoFindEmailRes;
import com.readyvery.readyverydemo.src.ceo.dto.CeoFindPasswordReq;
import com.readyvery.readyverydemo.src.ceo.dto.CeoFindPasswordRes;
import com.readyvery.readyverydemo.src.ceo.dto.CeoInfoRes;
import com.readyvery.readyverydemo.src.ceo.dto.CeoJoinReq;
import com.readyvery.readyverydemo.src.ceo.dto.CeoJoinRes;
import com.readyvery.readyverydemo.src.ceo.dto.CeoLogoutRes;
import com.readyvery.readyverydemo.src.ceo.dto.CeoMetaInfoRes;
import com.readyvery.readyverydemo.src.ceo.dto.CeoRemoveRes;
import com.readyvery.readyverydemo.src.ceo.dto.SimpleCeoInfoRes;

import jakarta.servlet.http.HttpServletResponse;

public interface CeoService {

	CeoAuthRes getCeoAuthByCustomUserDetails(CustomUserDetails userDetails);

	SimpleCeoInfoRes getSimpleCeoInfoById(Long id);

	CeoInfoRes getCeoInfoById(Long id);

	CeoLogoutRes removeRefreshTokenInDB(CustomUserDetails userDetails, HttpServletResponse response);

	CeoRemoveRes removeUser(CustomUserDetails userDetails, HttpServletResponse response) throws IOException;

	CeoJoinRes join(CeoJoinReq ceoJoinReq);

	CeoDuplicateCheckRes emailDuplicateCheck(CeoDuplicateCheckReq ceoDuplicateCheckReq);

	CeoMetaInfoRes entryReject(Long id);

	CeoFindEmailRes findCeoEmail(String phoneNumber);

	CeoFindPasswordRes findCeoPassword(CeoFindPasswordReq ceoFindPasswordReq);

	CeoExistEmailRes findCeoPasswordExistEmail(String email);
}
