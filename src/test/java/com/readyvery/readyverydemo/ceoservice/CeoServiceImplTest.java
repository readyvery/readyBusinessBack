// package com.readyvery.readyverydemo.ceoservice;
//
// import static org.junit.jupiter.api.Assertions.*;
//
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.test.context.ActiveProfiles;
// import org.springframework.transaction.annotation.Transactional;
//
// import com.readyvery.readyverydemo.domain.CeoInfo;
// import com.readyvery.readyverydemo.domain.Role;
// import com.readyvery.readyverydemo.domain.repository.CeoRepository;
// import com.readyvery.readyverydemo.src.ceo.CeoServiceImpl;
// import com.readyvery.readyverydemo.src.ceo.dto.CeoDuplicateCheckReq;
// import com.readyvery.readyverydemo.src.ceo.dto.CeoDuplicateCheckRes;
//
// @SpringBootTest
// @ActiveProfiles("test")
// @Transactional
// class CeoServiceImplTest {
//
// 	@Autowired
// 	private CeoServiceImpl ceoService;
//
// 	@Autowired
// 	private CeoRepository ceoRepository;
//
// 	@Test
// 	void testEmailDuplicateCheck() {
// 		// given
// 		CeoInfo ceoInfo = CeoInfo.builder()
// 			.email("existing@example.com")
// 			.password("password")
// 			.nickName("name")
// 			.phone("010-1234-5678")
// 			.role(Role.USER)
//
// 			.build();
//
// 		ceoRepository.save(ceoInfo);
//
// 		// when
// 		CeoDuplicateCheckReq request = CeoDuplicateCheckReq.builder()
// 			.email("existing@example.com")
// 			.build();
//
// 		CeoDuplicateCheckRes result = ceoService.emailDuplicateCheck(request);
//
// 		// 결과 검증
// 		assertFalse(result.isSuccess());
// 		assertEquals("이미 존재하는 이메일입니다.", result.getMessage());
// 	}
// }
//
