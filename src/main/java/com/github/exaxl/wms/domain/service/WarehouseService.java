package com.github.exaxl.wms.domain.service;

import org.springframework.stereotype.Service;

import com.github.exaxl.wms.adapter.out.db.repository.MockedWarehouseRepository;
import com.github.exaxl.wms.adapter.out.db.repository.MockedWarehouseRepository.WarehouseValidationProjection;
import com.github.exaxl.wms.domain.exception.WarehouseNotActiveException;
import com.github.exaxl.wms.domain.exception.WarehouseNotFoundException;
import com.github.exaxl.wms.domain.model.warehouse.WarehouseStatus;

@Service
public class WarehouseService {

	private final MockedWarehouseRepository mockedWarehouseRepository;

	public WarehouseService(MockedWarehouseRepository mockedWarehouseRepository) {
		this.mockedWarehouseRepository = mockedWarehouseRepository;
	}

	public WarehouseValidationProjection requireActiveWarehouse(int warehouseCode) {
		WarehouseValidationProjection warehouseInfo = mockedWarehouseRepository.findByCode(warehouseCode)
				.orElseThrow(() -> new WarehouseNotFoundException("Warehouse " + warehouseCode + " not found"));

		if (!WarehouseStatus.ACTIVE.equals(warehouseInfo.status())) {
			throw new WarehouseNotActiveException("Warehouse " + warehouseCode + " is not active");
		}

		return warehouseInfo;
	}
}
