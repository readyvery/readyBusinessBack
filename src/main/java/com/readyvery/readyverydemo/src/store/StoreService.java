package com.readyvery.readyverydemo.src.store;

import com.readyvery.readyverydemo.src.store.dto.StoreStatusRes;

public interface StoreService {

	StoreStatusRes getStoreStatusById(Long id);

	StoreStatusRes updateStoreStatusById(Long id, boolean status);
}
