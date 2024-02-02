package com.readyvery.readyverydemo.duplicate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.readyvery.readyverydemo.domain.repository.CeoRepository;
import com.readyvery.readyverydemo.src.ceo.CeoServiceImpl;
import com.readyvery.readyverydemo.src.ceo.dto.CeoDuplicateCheckReq;
import com.readyvery.readyverydemo.src.ceo.dto.CeoDuplicateCheckRes;

@ExtendWith(MockitoExtension.class)
public class EmailDuplicateCheckTest {
	@Mock
	private CeoRepository ceoRepository;

	@InjectMocks
	private CeoServiceImpl ceoService;

	@Test
	void emailDuplicateCheckWithDuplicateEmail() {

		//given
		CeoDuplicateCheckReq request = new CeoDuplicateCheckReq("123v131@gmail.com");

		// when
		// ceoRepository.existsByEmail(anyString())가 true를 반환하도록 설정
		when(ceoRepository.existsByEmail("123v131@gmail.com")).thenReturn(true);

		// then
		CeoDuplicateCheckRes response = ceoService.emailDuplicateCheck(request);

		assertFalse(response.isSuccess());
		assertEquals("이미 존재하는 이메일입니다.", response.getMessage());
		System.out.println("response = " + response.getMessage());
	}

	@Test
	void emailDuplicateCheckWithNotDuplicateEmail() {

		//given
		CeoDuplicateCheckReq request = new CeoDuplicateCheckReq("123v131@gmail.com");

		// when
		// ceoRepository.existsByEmail(anyString())가 false를 반환하도록 설정
		when(ceoRepository.existsByEmail("123v131@gmail.com")).thenReturn(false);

		// then
		CeoDuplicateCheckRes response = ceoService.emailDuplicateCheck(request);

		assertTrue(response.isSuccess());
		assertEquals("사용 가능한 이메일입니다.", response.getMessage());
		System.out.println("response = " + response.getMessage());
	}

	@Test
	void emailDuplicateCheckWithEmptyEmail() {

		//given
		CeoDuplicateCheckReq request = new CeoDuplicateCheckReq("");

		// when
		// ceoRepository.existsByEmail(anyString())가 false를 반환하도록 설정
		//when(ceoRepository.existsByEmail("")).thenReturn(false); <= 해당 코드는 실행되지 않음

		// then
		CeoDuplicateCheckRes response = ceoService.emailDuplicateCheck(request);

		assertFalse(response.isSuccess());
		assertEquals("이메일을 입력해주세요.", response.getMessage());
		System.out.println("response = " + response.getMessage());
	}
}
