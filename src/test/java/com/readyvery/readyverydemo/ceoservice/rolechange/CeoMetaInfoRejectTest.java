package com.readyvery.readyverydemo.ceoservice.rolechange;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.readyvery.readyverydemo.domain.CeoInfo;
import com.readyvery.readyverydemo.domain.repository.CeoRepository;
import com.readyvery.readyverydemo.src.ceo.CeoServiceFacade;
import com.readyvery.readyverydemo.src.ceo.CeoServiceImpl;
import com.readyvery.readyverydemo.src.ceo.dto.CeoMetaInfoRes;

@ExtendWith(MockitoExtension.class)
public class CeoMetaInfoRejectTest {

	@Mock
	private CeoRepository ceoRepository;

	@Mock
	private CeoServiceFacade ceoServiceFacade;

	@InjectMocks
	private CeoServiceImpl ceoService;

	@Test
	public void testEntryReject_Success() {
		Long id = 1L;
		CeoInfo mockCeoInfo = mock(CeoInfo.class);
		when(ceoServiceFacade.getCeoInfo(id)).thenReturn(mockCeoInfo);

		// when
		CeoMetaInfoRes result = ceoService.entryReject(id);

		// Then
		verify(mockCeoInfo, times(1)).rejectEntry();
		verify(ceoRepository, times(1)).save(mockCeoInfo);
		assertTrue(result.isSuccess());
		assertEquals("다시 입점 신청 부탁드립니다.", result.getMessage());
	}

	@Test
	public void testEntryReject_CeoInfoNotFound() {
		// Given
		Long id = 2L;
		when(ceoServiceFacade.getCeoInfo(id)).thenReturn(
			null); // ID로 CEO 정보를 찾을 수 없을 때, Optional.empty()를 반환하도록 설정합니다.

		// When & Then
		assertThrows(NullPointerException.class, () -> {
			ceoService.entryReject(id);
		});
	}

	@Test
	public void testEntryReject_SaveThrowsException() {
		// Given
		Long id = 3L;
		CeoInfo mockCeoInfo = mock(CeoInfo.class);
		when(ceoServiceFacade.getCeoInfo(id)).thenReturn(mockCeoInfo);
		doThrow(new RuntimeException("Database error")).when(ceoRepository).save(any(CeoInfo.class));

		// When & Then
		Exception exception = assertThrows(RuntimeException.class, () -> {
			ceoService.entryReject(id);
		});
		assertEquals("Database error", exception.getMessage());
	}

}
