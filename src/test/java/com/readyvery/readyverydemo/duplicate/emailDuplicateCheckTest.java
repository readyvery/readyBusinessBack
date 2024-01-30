package com.readyvery.readyverydemo.duplicate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.readyvery.readyverydemo.domain.repository.CeoRepository;
import com.readyvery.readyverydemo.src.ceo.CeoService;
import com.readyvery.readyverydemo.src.ceo.dto.CeoDuplicateCheckReq;
import com.readyvery.readyverydemo.src.ceo.dto.CeoDuplicateCheckRes;

@ExtendWith(MockitoExtension.class)
public class emailDuplicateCheckTest {

	@Mock
	private CeoRepository ceoRepository;
	@InjectMocks
	private CeoService ceoServiceImpl;

	@Test
	void emailDuplicateCheckWithDuplicateEmail() {

		when(ceoRepository.existsByEmail(anyString())).thenReturn(true);
		CeoDuplicateCheckReq request = new CeoDuplicateCheckReq("123v131@gmail.com");
		CeoDuplicateCheckRes response = ceoServiceImpl.emailDuplicateCheck(request);
		assertFalse(response.isSuccess());
		assertEquals("이메일을 입력해주세요.", response.getMessage());

	}
}
