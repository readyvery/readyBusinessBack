package com.readyvery.readyverydemo.src.entryapplication;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.readyvery.readyverydemo.config.S3Config;
import com.readyvery.readyverydemo.domain.CeoInfo;
import com.readyvery.readyverydemo.domain.CeoMetaInfo;
import com.readyvery.readyverydemo.domain.Role;
import com.readyvery.readyverydemo.domain.repository.CeoMetaRepository;
import com.readyvery.readyverydemo.global.exception.BusinessLogicException;
import com.readyvery.readyverydemo.global.exception.ExceptionCode;
import com.readyvery.readyverydemo.src.ceo.CeoService;
import com.readyvery.readyverydemo.src.ceo.CeoServiceFacade;
import com.readyvery.readyverydemo.src.entryapplication.dto.CeoEntryApplicationReq;
import com.readyvery.readyverydemo.src.entryapplication.dto.CeoEntryApplicationRes;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CeoEntryApplicationServiceImpl implements CeoEntryApplicationService {

	private final AmazonS3Client amazonS3Client;
	private final S3Config s3Config;
	private final CeoService ceoServiceImpl;
	private final CeoMetaRepository ceoMetaRepository;
	private final CeoServiceFacade ceoServiceFacade;

	@Override
	public CeoEntryApplicationRes entryApplication(Long userId, CeoEntryApplicationReq ceoEntryApplicationReq) {
		// 1. 유저 정보 확인
		CeoInfo ceoInfo = ceoServiceFacade.getCeoInfo(userId);
		System.out.println("ceoInfo.getRole() = " + ceoInfo.getRole());
		System.out.println("Role = " + Role.USER);
		if (!ceoInfo.getRole().equals(Role.USER)) {
			throw new BusinessLogicException(ExceptionCode.AUTH_ERROR);
		}

		// 2. 파일 업로드
		List<MultipartFile> files = Arrays.asList(
			ceoEntryApplicationReq.getBusinessLicense(),
			ceoEntryApplicationReq.getBusinessReport(),
			ceoEntryApplicationReq.getIdentityCard(),
			ceoEntryApplicationReq.getBankAccount()
		);

		List<String> fileNames = files.stream()
			.map(file -> createSaveFileName() + "_" + file.getOriginalFilename()) // 파일명 생성
			.toList();

		List<CompletableFuture<Void>> uploadFutures = IntStream.range(0, files.size())
			.filter(i -> files.get(i) != null && !files.get(i).isEmpty())
			.mapToObj(i -> CompletableFuture.runAsync(() -> {
				try {
					String fileName = fileNames.get(i); // 파일명 가져오기
					MultipartFile file = files.get(i); // 파일 가져오기
					uploadFileToS3(fileName, file); // 파일명을 추가하여 업로드 메소드 호출
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}))
			.toList();

		CompletableFuture.allOf(uploadFutures.toArray(new CompletableFuture[0])).join();

		// 3. 파일 정보 저장
		ceoMetaInfoSave(ceoInfo, ceoEntryApplicationReq, fileNames);

		// 4. 유저 권한 변경
		ceoServiceFacade.changeRoleAndSave(ceoInfo.getId(), Role.READY);

		return CeoEntryApplicationRes.builder()
			.success(true)
			.message("성공")
			.build();
	}

	@Override
	public void uploadFileToS3(String fileName, MultipartFile file) throws IOException {

		// 2. 서버에 파일 저장 & DB에 파일 정보(fileinfo) 저장
		// - 동일 파일명을 피하기 위해 random값 사용

		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentType(file.getContentType());
		metadata.setContentLength(file.getSize());

		amazonS3Client.putObject(s3Config.getBucket(), fileName, file.getInputStream(), metadata);

	}

	@Override
	public void ceoMetaInfoSave(CeoInfo ceoInfo, CeoEntryApplicationReq ceoEntryApplicationReq,
		List<String> fileNames) {

		System.out.println("fileNames.get(0) = " + fileNames.get(0));
		// 2-3. DB에 정보 저장

		// - 파일 정보 저장
		CeoMetaInfo ceoMetaInfo = CeoMetaInfo.builder()
			.ceoInfo(ceoInfo)
			.storeName(ceoEntryApplicationReq.getStoreName())
			.storeAddress(ceoEntryApplicationReq.getStoreAddress())
			.registrationNumber(ceoEntryApplicationReq.getRegistrationNumber())
			.businessLicenseFileName(fileNames.get(0))
			.businessReportFileName(fileNames.get(1))
			.identityCardFileName(fileNames.get(2))
			.bankAccountFileName(fileNames.get(3))
			.build();

		ceoMetaRepository.save(ceoMetaInfo);
	}

	// 파일 저장 이름 만들기
	// - 사용자들이 올리는 파일 이름이 같을 수 있으므로, 자체적으로 랜덤 이름을 만들어 사용한다
	private String createSaveFileName() {

		return UUID.randomUUID().toString();
	}

}
