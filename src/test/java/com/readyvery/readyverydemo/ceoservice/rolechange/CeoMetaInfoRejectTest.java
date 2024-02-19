package com.readyvery.readyverydemo.ceoservice.rolechange;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.readyvery.readyverydemo.domain.CeoInfo;
import com.readyvery.readyverydemo.domain.repository.CeoRepository;
import com.readyvery.readyverydemo.global.exception.BusinessLogicException;
import com.readyvery.readyverydemo.src.ceo.CeoServiceImpl;
import com.readyvery.readyverydemo.src.ceo.dto.CeoMetaInfoRes;

@ExtendWith(MockitoExtension.class)
public class CeoMetaInfoRejectTest {

	@Mock
	private CeoRepository ceoRepository;

	@InjectMocks
	private CeoServiceImpl ceoService;

	@Test
	public void testEntryReject_Success() {
		// Given
		Long id = 1L;
		CeoInfo mockCeoInfo = mock(CeoInfo.class);
		when(ceoRepository.findById(id)).thenReturn(Optional.of(mockCeoInfo));

		// When
		CeoMetaInfoRes ceoMetaInfoRes = ceoService.entryReject(id);

		// Then
		verify(mockCeoInfo, times(1)).rejectEntry(); // rejectEntry가 한 번 호출되었는지 확인합니다.
		verify(ceoRepository, times(1)).save(mockCeoInfo); // save가 한 번 호출되었는지 확인합니다.
		assertTrue(ceoMetaInfoRes.isSuccess()); // 결과 객체의 성공 여부를 검증합니다.
		assertEquals("다시 입점 신청 부탁드립니다.", ceoMetaInfoRes.getMessage()); // 결과 메시지가 예상과 일치하는지 확인합니다.
	}

	@Test
	public void testEntryReject_CeoInfoNotFound() {
		// Given
		Long id = 2L;
		when(ceoRepository.findById(id)).thenReturn(
			Optional.empty()); // ID로 CEO 정보를 찾을 수 없을 때, Optional.empty()를 반환하도록 설정합니다.

		// When & Then
		assertThrows(BusinessLogicException.class, () -> {
			ceoService.entryReject(id);
		});
	}

	@Test
	public void testEntryReject_SaveThrowsException() {
		// Given
		Long id = 3L;
		CeoInfo mockCeoInfo = mock(CeoInfo.class);
		when(ceoRepository.findById(id)).thenReturn(Optional.of(mockCeoInfo));
		doThrow(new RuntimeException("Database error")).when(ceoRepository).save(any(CeoInfo.class));

		// When & Then
		Exception exception = assertThrows(RuntimeException.class, () -> {
			ceoService.entryReject(id);
		});
		assertEquals("Database error", exception.getMessage());
	}

}
