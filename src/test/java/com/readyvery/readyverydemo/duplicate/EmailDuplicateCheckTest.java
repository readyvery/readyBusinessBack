package com.readyvery.readyverydemo.duplicate;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.readyvery.readyverydemo.domain.CeoInfo;
import com.readyvery.readyverydemo.domain.Role;
import com.readyvery.readyverydemo.domain.repository.CeoRepository;
import com.readyvery.readyverydemo.src.ceo.CeoService;
import com.readyvery.readyverydemo.src.ceo.dto.CeoDuplicateCheckReq;
import com.readyvery.readyverydemo.src.ceo.dto.CeoDuplicateCheckRes;

@ExtendWith(MockitoExtension.class)
public class EmailDuplicateCheckTest {
	@Mock
	private CeoRepository ceoRepository;

	@Mock
	private CeoService ceoServiceImpl;

	@BeforeEach
	public void setup() {
		CeoInfo request = CeoInfo.builder()
			.email("123v131@gmail.com")
			.password("1234")
			.nickName("test")
			.phone("01012341234")
			.role(Role.GUEST)
			.build();
		;
		ceoRepository.save(request);
	}

	@Test
	void emailDuplicateCheckWithDuplicateEmail() {

		CeoDuplicateCheckReq request = new CeoDuplicateCheckReq("123v131@gmail.com");
		CeoDuplicateCheckRes mockResponse = new CeoDuplicateCheckRes(false, "이메일 중복입니다.");

		when(ceoRepository.existsByEmail("123v131@gmail.com")).thenReturn(true);
		//when(ceoServiceImpl.emailDuplicateCheck(request)).thenReturn(mockResponse);

		CeoDuplicateCheckRes response = ceoServiceImpl.emailDuplicateCheck(request);

		System.out.println("response = " + response.isSuccess());
		//assertFalse(response.isSuccess());
		//assertEquals("이메일 중복입니다.", response.getMessage());

	}
}
