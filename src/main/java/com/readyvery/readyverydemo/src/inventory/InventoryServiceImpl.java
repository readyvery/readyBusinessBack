package com.readyvery.readyverydemo.src.inventory;

import java.util.List;

import org.springframework.stereotype.Service;

import com.readyvery.readyverydemo.domain.CeoInfo;
import com.readyvery.readyverydemo.domain.Foodie;
import com.readyvery.readyverydemo.domain.FoodieCategory;
import com.readyvery.readyverydemo.domain.repository.CeoRepository;
import com.readyvery.readyverydemo.domain.repository.InventoryRepository;
import com.readyvery.readyverydemo.global.exception.BusinessLogicException;
import com.readyvery.readyverydemo.global.exception.ExceptionCode;
import com.readyvery.readyverydemo.src.inventory.dto.InventoryMapper;
import com.readyvery.readyverydemo.src.inventory.dto.InventoryStockRes;
import com.readyvery.readyverydemo.src.inventory.dto.InventoryUpdateReq;
import com.readyvery.readyverydemo.src.inventory.dto.InventoryUpdateRes;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

	private final CeoRepository ceoRepository;
	private final InventoryMapper inventoryMapper;
	private final InventoryRepository inventoryRepository;

	@Override
	public InventoryStockRes getInventorys(Long id) {
		CeoInfo ceoInfo = getCeoInfo(id);
		List<FoodieCategory> foodieCategories = ceoInfo.getStore().getFoodieCategories();

		return inventoryMapper.foodiesToInventoryStockRes(foodieCategories);
	}

	@Override
	public InventoryUpdateRes updateInventorys(Long id, InventoryUpdateReq request) {
		CeoInfo ceoInfo = getCeoInfo(id);
		Foodie foodie = getFoodie(request.getIdx());
		verifyCeoInfoAndFoodie(ceoInfo, foodie);
		foodie.updateCheckSoldOut(request.isSoldOut());
		inventoryRepository.save(foodie);

		return InventoryUpdateRes.builder()
			.message("재고 수정 완료")
			.success(true)
			.build();
	}

	private void verifyCeoInfoAndFoodie(CeoInfo ceoInfo, Foodie foodie) {
		if (!ceoInfo.getStore().equals(foodie.getFoodieCategory().getStore())) {
			throw new BusinessLogicException(ExceptionCode.FOODIE_NOT_MATCHED_STORE);
		}
	}

	private CeoInfo getCeoInfo(Long id) {
		return ceoRepository.findById(id).orElseThrow(
			() -> new BusinessLogicException(ExceptionCode.USER_NOT_FOUND)
		);
	}

	private Foodie getFoodie(Long id) {
		return inventoryRepository.findById(id).orElseThrow(
			() -> new BusinessLogicException(ExceptionCode.FOODIE_NOT_FOUND)
		);
	}
}
