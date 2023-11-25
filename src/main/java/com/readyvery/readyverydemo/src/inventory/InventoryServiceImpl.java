package com.readyvery.readyverydemo.src.inventory;

import java.util.List;

import org.springframework.stereotype.Service;

import com.readyvery.readyverydemo.domain.CeoInfo;
import com.readyvery.readyverydemo.domain.FoodieCategory;
import com.readyvery.readyverydemo.domain.repository.CeoRepository;
import com.readyvery.readyverydemo.global.exception.BusinessLogicException;
import com.readyvery.readyverydemo.global.exception.ExceptionCode;
import com.readyvery.readyverydemo.src.inventory.dto.InventoryMapper;
import com.readyvery.readyverydemo.src.inventory.dto.InventoryStockRes;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

	private final CeoRepository ceoRepository;
	private final InventoryMapper inventoryMapper;

	@Override
	public InventoryStockRes getInventorys(Long id) {
		CeoInfo ceoInfo = getCeoInfo(id);
		List<FoodieCategory> foodieCategories = ceoInfo.getStore().getFoodieCategories();

		return inventoryMapper.foodiesToInventoryStockRes(foodieCategories);
	}

	private CeoInfo getCeoInfo(Long id) {
		return ceoRepository.findById(id).orElseThrow(
			() -> new BusinessLogicException(ExceptionCode.USER_NOT_FOUND)
		);
	}
}
