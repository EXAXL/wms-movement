package com.github.exaxl.wms.domain.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.ZoneId;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;

import com.github.exaxl.wms.adapter.out.db.repository.MockedWarehouseRepository;
import com.github.exaxl.wms.adapter.out.db.repository.MockedWarehouseRepository.WarehouseValidationProjection;
import com.github.exaxl.wms.domain.exception.WarehouseNotActiveException;
import com.github.exaxl.wms.domain.exception.WarehouseNotFoundException;
import com.github.exaxl.wms.domain.model.warehouse.WarehouseStatus;

class WarehouseServiceTest {

	private MockedWarehouseRepository mockedWarehouseRepository = Mockito.mock(MockedWarehouseRepository.class);
	private WarehouseService service = new WarehouseService(mockedWarehouseRepository);

	@Test
	void requireActiveWarehouse_WhenWarehouseNotFound_ThenThrowWarehouseNotFoundException() {
		int warehouseCode = 123;
		Mockito.when(mockedWarehouseRepository.findByCode(warehouseCode)).thenReturn(Optional.empty());

		Assertions.assertThatThrownBy(() -> service.requireActiveWarehouse(warehouseCode))
				.isInstanceOf(WarehouseNotFoundException.class)
				.hasMessageContaining("Warehouse " + warehouseCode + " not found");
	}


	@ParameterizedTest(name = "Warehouse status: {0} -> WarehouseNotActiveException")
	@ValueSource(strings = {"INACTIVE", "MAINTENANCE"})
	void requireActiveWarehouse_WhenWarehouseIsNotActive_ThenThrowWarehouseNotActiveException(String status) {
		int warehouseCode = 123;
		WarehouseValidationProjection warehouseInfo = new WarehouseValidationProjection(warehouseCode, WarehouseStatus.of(status), ZoneId.of("UTC"));
		Mockito.when(mockedWarehouseRepository.findByCode(warehouseCode)).thenReturn(Optional.of(warehouseInfo));

		Assertions.assertThatThrownBy(() -> service.requireActiveWarehouse(warehouseCode))
				.isInstanceOf(WarehouseNotActiveException.class)
				.hasMessageContaining("Warehouse " + warehouseCode + " is not active");
	}
	
	@Test
	void requireActiveWarehouse_WhenWarehouseIsActive_ThenReturnWarehouseInfo() {
		int warehouseCode = 123;
		WarehouseValidationProjection warehouseInfo = new WarehouseValidationProjection(warehouseCode, WarehouseStatus.of("ACTIVE"), ZoneId.of("UTC"));
		Mockito.when(mockedWarehouseRepository.findByCode(warehouseCode)).thenReturn(Optional.of(warehouseInfo));

		WarehouseValidationProjection result = service.requireActiveWarehouse(warehouseCode);

		assertThat(result).isEqualTo(warehouseInfo);
	}
}
