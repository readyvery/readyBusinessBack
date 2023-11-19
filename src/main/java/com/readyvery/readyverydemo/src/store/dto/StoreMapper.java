package com.readyvery.readyverydemo.src.store.dto;

import org.springframework.stereotype.Component;

@Component
public class StoreMapper {

	public StoreStatusRes storeStatusToStoreStatusRes(boolean status) {
		return StoreStatusRes.builder()
			.status(status)
			.build();
	}
}
