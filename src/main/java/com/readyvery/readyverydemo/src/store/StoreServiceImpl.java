package com.readyvery.readyverydemo.src.store;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.readyvery.readyverydemo.domain.CeoInfo;
import com.readyvery.readyverydemo.domain.repository.CeoRepository;
import com.readyvery.readyverydemo.domain.repository.StoreRepository;
import com.readyvery.readyverydemo.global.exception.BusinessLogicException;
import com.readyvery.readyverydemo.global.exception.ExceptionCode;
import com.readyvery.readyverydemo.src.store.dto.StoreMapper;
import com.readyvery.readyverydemo.src.store.dto.StoreStatusRes;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class StoreServiceImpl implements StoreService {

	private final StoreRepository storeRepository;
	private final CeoRepository ceoRepository;
	private final StoreMapper storeMapper;

	@Override
	public StoreStatusRes getStoreStatusById(Long id) {
		CeoInfo ceoInfo = getCeoInfo(id);
		return storeMapper.storeStatusToStoreStatusRes(ceoInfo.getStore().isStatus());
	}

	private CeoInfo getCeoInfo(Long id) {
		return ceoRepository.findById(id).orElseThrow(
			() -> new BusinessLogicException(ExceptionCode.USER_NOT_FOUND)
		);
	}
}
