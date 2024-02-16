package com.readyvery.readyverydemo.src.entryapplication;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.readyvery.readyverydemo.domain.CeoInfo;
import com.readyvery.readyverydemo.src.entryapplication.dto.CeoEntryApplicationReq;
import com.readyvery.readyverydemo.src.entryapplication.dto.CeoEntryApplicationRes;

public interface CeoEntryApplicationService {
	CeoEntryApplicationRes entryApplication(Long userId, CeoEntryApplicationReq ceoEntryApplicationReq);

	void uploadFileToS3(String fileName, MultipartFile file) throws IOException;

	void ceoMetaInfoSave(CeoInfo ceoInfo, CeoEntryApplicationReq ceoEntryApplicationReq, List<String> fileNames);

}
