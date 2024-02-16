package com.readyvery.readyverydemo.src.ceo;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.readyvery.readyverydemo.config.CeoApiConfig;
import com.readyvery.readyverydemo.domain.CeoInfo;
import com.readyvery.readyverydemo.domain.CeoMetaInfo;
import com.readyvery.readyverydemo.domain.Role;
import com.readyvery.readyverydemo.domain.repository.CeoMetaRepository;
import com.readyvery.readyverydemo.domain.repository.CeoRepository;
import com.readyvery.readyverydemo.global.exception.BusinessLogicException;
import com.readyvery.readyverydemo.global.exception.ExceptionCode;
import com.readyvery.readyverydemo.redis.dao.RefreshToken;
import com.readyvery.readyverydemo.redis.repository.RefreshTokenRepository;
import com.readyvery.readyverydemo.security.jwt.dto.CustomUserDetails;
import com.readyvery.readyverydemo.src.ceo.dto.CeoAuthRes;
import com.readyvery.readyverydemo.src.ceo.dto.CeoDuplicateCheckReq;
import com.readyvery.readyverydemo.src.ceo.dto.CeoDuplicateCheckRes;
import com.readyvery.readyverydemo.src.ceo.dto.CeoInfoRes;
import com.readyvery.readyverydemo.src.ceo.dto.CeoJoinReq;
import com.readyvery.readyverydemo.src.ceo.dto.CeoJoinRes;
import com.readyvery.readyverydemo.src.ceo.dto.CeoLogoutRes;
import com.readyvery.readyverydemo.src.ceo.dto.CeoMapper;
import com.readyvery.readyverydemo.src.ceo.dto.CeoMetaInfoReq;
import com.readyvery.readyverydemo.src.ceo.dto.CeoMetaInfoRes;
import com.readyvery.readyverydemo.src.ceo.dto.CeoRemoveRes;
import com.readyvery.readyverydemo.src.smsauthentication.VerificationService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class CeoServiceImpl implements CeoService {

	private final CeoRepository ceoRepository;
	private final CeoMapper ceoMapper;
	private final CeoApiConfig ceoApiConfig;
	private final PasswordEncoder passwordEncoder;
	private final RefreshTokenRepository refreshTokenRepository;
	private final VerificationService verificationService;
	private final CeoMetaRepository ceoMetaRepository;

	private AmazonS3 amazonS3Client;

	@Override
	public CeoAuthRes getCeoAuthByCustomUserDetails(CustomUserDetails userDetails) {
		verifyUserDetails(userDetails);
		return ceoMapper.ceoInfoToCeoAuthRes(userDetails);

	}

	@Override
	public CeoInfoRes getCeoInfoById(Long id) {
		CeoInfo ceoInfo = getCeoInfo(id);
		return ceoMapper.ceoInfoToCeoInfoRes(ceoInfo);
	}

	@Override
	public CeoLogoutRes removeRefreshTokenInDB(CustomUserDetails userDetails, HttpServletResponse response) {

		invalidateRefreshTokenCookie(response); // 쿠키 무효화
		removeRefreshTokenInRedis(userDetails); // Redis에서 Refresh Token 삭제
		return CeoLogoutRes.builder()
			.success(true)
			.message("로그아웃 성공")
			.build();
	}

	@Override
	public CeoRemoveRes removeUser(CustomUserDetails userDetails, HttpServletResponse response) throws IOException {
		CeoInfo user = getCeoInfo(userDetails.getId());
		requestToServer("KakaoAK " + ceoApiConfig.getServiceAppAdminKey(),
			"target_id_type=user_id&target_id=" + user.getSocialId());
		removeRefreshTokenInRedis(userDetails); // Redis에서 Refresh Token 삭제
		invalidateRefreshTokenCookie(response); // 쿠키 무효화
		return CeoRemoveRes.builder()
			.message("회원 탈퇴가 완료되었습니다.")
			.success(true)
			.build();
	}

	@Override
	public CeoJoinRes join(CeoJoinReq ceoJoinReq) {
		if (ceoJoinReq.getPassword().equals(ceoJoinReq.getConfirmPassword())) {
			if (ceoRepository.existsByEmail(ceoJoinReq.getEmail())) {
				return CeoJoinRes.builder()
					.success(false)
					.message("이미 존재하는 이메일입니다.")
					.build();
			} else {
				if (!verificationService.verifyNumber(ceoJoinReq.getPhone())) {
					return CeoJoinRes.builder()
						.success(false)
						.message("인증되지 않은 전화번호 입니다.")
						.build();
				} else {
					CeoInfo ceoInfo = ceoMapper.ceoJoinReqToCeoInfo(ceoJoinReq);
					verifyCeoJoin(ceoInfo);
					ceoInfo.encodePassword(passwordEncoder);
					ceoRepository.save(ceoInfo);
					return CeoJoinRes.builder()
						.success(true)
						.message("회원가입이 완료되었습니다.")
						.build();
				}
			}

		} else {
			return CeoJoinRes.builder()
				.success(false)
				.message("비밀번호가 일치하지 않습니다.")
				.build();
		}
	}

	private void verifyCeoJoin(CeoInfo ceoInfo) {
		if (ceoRepository.existsByEmail(ceoInfo.getEmail())) {
			throw new BusinessLogicException(ExceptionCode.EMAIL_DUPLICATION);
		}

	}

	/**
	 * 로그아웃
	 * @param response
	 */
	private void invalidateRefreshTokenCookie(HttpServletResponse response) {
		Cookie refreshTokenCookie = new Cookie(ceoApiConfig.getRefreshCookie(), null); // 쿠키 이름을 동일하게 설정
		refreshTokenCookie.setHttpOnly(true);
		refreshTokenCookie.setPath("/api/v1/refresh/token"); // 기존과 동일한 경로 설정
		refreshTokenCookie.setMaxAge(0); // 만료 시간을 0으로 설정하여 즉시 만료
		response.addCookie(refreshTokenCookie);
	}

	@Override
	public CeoInfo getCeoInfo(Long id) {
		return ceoRepository.findById(id).orElseThrow(
			() -> new BusinessLogicException(ExceptionCode.USER_NOT_FOUND)
		);
	}

	@Override
	public void changeRoleAndSave(Long userId, Role role) {
		CeoInfo ceoInfo = getCeoInfo(userId);
		ceoInfo.changeRole(role);
		ceoRepository.save(ceoInfo);
	}

	@Override
	public CeoDuplicateCheckRes emailDuplicateCheck(CeoDuplicateCheckReq ceoDuplicateCheckReq) {

		if (ceoDuplicateCheckReq.getEmail() == null || ceoDuplicateCheckReq.getEmail().isEmpty()) {
			return CeoDuplicateCheckRes.builder()
				.success(false)
				.message("이메일을 입력해주세요.")
				.build();
		} else if (ceoRepository.existsByEmail(ceoDuplicateCheckReq.getEmail())) {
			return CeoDuplicateCheckRes.builder()
				.success(false)
				.message("이미 존재하는 이메일입니다.")
				.build();
		}
		return CeoDuplicateCheckRes.builder()
			.success(true)
			.message("사용 가능한 이메일입니다.")
			.build();
	}

	@Override
	public CeoMetaInfoRes getCeoMetaInfo(Long id, CeoMetaInfoReq ceoMetaInfoReq) throws
		IOException,
		ExecutionException,
		InterruptedException {
		// 1. 유저 정보 확인
		CeoInfo ceoInfo = getCeoInfo(id);
		if (ceoInfo.getRole() != Role.USER) {
			throw new BusinessLogicException(ExceptionCode.AUTH_ERROR);
		}

		// 2. 서버에 파일 저장 & DB에 파일 정보(fileinfo) 저장
		// - 동일 파일명을 피하기 위해 random값 사용
		String businessLicenseSaveFileName = createSaveFileName(
			ceoMetaInfoReq.getBusinessLicense().getOriginalFilename());
		String businessReportSaveFileName = createSaveFileName(
			ceoMetaInfoReq.getBusinessReport().getOriginalFilename());
		String identityCardSaveFileName = createSaveFileName(ceoMetaInfoReq.getIdentityCard().getOriginalFilename());
		String bankAccountSaveFileName = createSaveFileName(ceoMetaInfoReq.getBankAccount().getOriginalFilename());

		// 2-1.서버에 파일 저장
		ceoMetaInfoReq.getBusinessLicense().transferTo(new File(getFullPath(businessLicenseSaveFileName)));
		ceoMetaInfoReq.getBusinessReport().transferTo(new File(getFullPath(businessReportSaveFileName)));
		ceoMetaInfoReq.getIdentityCard().transferTo(new File(getFullPath(identityCardSaveFileName)));
		ceoMetaInfoReq.getBankAccount().transferTo(new File(getFullPath(bankAccountSaveFileName)));

		List<MultipartFile> files = List.of(
			ceoMetaInfoReq.getBusinessLicense(),
			ceoMetaInfoReq.getBusinessReport(),
			ceoMetaInfoReq.getIdentityCard(),
			ceoMetaInfoReq.getBankAccount());

		List<CompletableFuture<Void>> uploadJobs = files.stream().map(file -> CompletableFuture.runAsync(() -> {
			if (file != null && !file.isEmpty()) {
				try {
					ObjectMetadata objectMetadata = new ObjectMetadata();
					objectMetadata.setContentType(file.getContentType());
					objectMetadata.setContentLength(file.getSize());

					// 예시로 objectKey 설정, 실제로는 파일 이름이나 다른 식별자를 기반으로 설정할 수 있습니다.
					String objectKey = "your-directory/" + file.getOriginalFilename();

					PutObjectRequest putObjectRequest = new PutObjectRequest(
						"bucketName",
						objectKey,
						file.getInputStream(),
						objectMetadata);

					amazonS3Client.putObject(putObjectRequest);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		})).toList();

		CompletableFuture.allOf(uploadJobs.toArray(new CompletableFuture[0])).get();

		// 2-3. DB에 정보 저장
		// - 파일 정보 저장
		CeoMetaInfo ceoMetaInfo = CeoMetaInfo.builder()
			.ceoInfo(ceoInfo)
			.storeName(ceoMetaInfoReq.getStoreName())
			.storeAddress(ceoMetaInfoReq.getStoreAddress())
			.registrationNumber(ceoMetaInfoReq.getRegistrationNumber())
			.businessLicenseFileName(businessLicenseSaveFileName)
			.businessReportFileName(businessReportSaveFileName)
			.identityCardFileName(identityCardSaveFileName)
			.bankAccountFileName(bankAccountSaveFileName)
			.build();

		ceoMetaRepository.save(ceoMetaInfo);

		// 3. 유저 권한 변경
		changeRoleAndSave(id, Role.READY);

		return CeoMetaInfoRes.builder()
			.success(true)
			.message("입점신청 완료")
			.build();
	}

	// 파일 저장 이름 만들기
	// - 사용자들이 올리는 파일 이름이 같을 수 있으므로, 자체적으로 랜덤 이름을 만들어 사용한다
	private String createSaveFileName(String originalFilename) {
		String ext = extractExt(originalFilename);
		String uuid = UUID.randomUUID().toString();
		return uuid + "." + ext;
	}

	// 확장자명 구하기
	private String extractExt(String originalFilename) {
		int pos = originalFilename.lastIndexOf(".");
		return originalFilename.substring(pos + 1);
	}

	// fullPath 만들기
	private String getFullPath(String filename) {

		return ceoApiConfig.getUploadPath() + File.separator + filename;
	}

	private void removeRefreshTokenInRedis(CustomUserDetails userDetails) {
		RefreshToken refreshToken = refreshTokenRepository.findById(userDetails.getEmail())
			.orElseThrow(() -> new UsernameNotFoundException("해당 이메일이 존재하지 않습니다."));
		refreshTokenRepository.delete(refreshToken);
	}

	private void verifyUserDetails(CustomUserDetails userDetails) {
		if (userDetails == null) {
			throw new BusinessLogicException(ExceptionCode.AUTH_ERROR);
		}
	}

	private void requestToServer(String headerStr, String postData) throws IOException {
		URL url = new URL("https://kapi.kakao.com/v1/user/unlink");
		HttpURLConnection connectReq = null;

		try {
			connectReq = (HttpURLConnection)url.openConnection();
			connectReq.setRequestMethod("POST");
			connectReq.setDoOutput(true); // Enable writing to the connection output stream

			// Set headers
			connectReq.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			if (headerStr != null && !headerStr.isEmpty()) {
				connectReq.setRequestProperty("Authorization", headerStr);
			}

			// Write the post data to the request body
			try (OutputStream os = connectReq.getOutputStream()) {
				byte[] input = postData.getBytes(StandardCharsets.UTF_8);
				os.write(input, 0, input.length);
			}

			int responseCode = connectReq.getResponseCode();
			try (BufferedReader br = new BufferedReader(new InputStreamReader(
				responseCode == 200 ? connectReq.getInputStream() : connectReq.getErrorStream()))) {
				String inputLine;
				StringBuilder response = new StringBuilder();

				while ((inputLine = br.readLine()) != null) {
					response.append(inputLine);
				}
			}
		} finally {
			if (connectReq != null) {
				connectReq.disconnect();
			}
		}
	}

}
