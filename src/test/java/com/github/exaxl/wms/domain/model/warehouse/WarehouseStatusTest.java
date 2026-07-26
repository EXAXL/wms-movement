package com.github.exaxl.wms.domain.model.warehouse;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import com.github.exaxl.error.exception.core.ValidationException;
import com.github.exaxl.wms.domain.enums.WarehouseErrorCode;

class WarehouseStatusTest {

	@ParameterizedTest
	@NullAndEmptySource
	void constructor_WithInvalidInput_ThenThrowException(String invalidInput) {
		Assertions.assertThatThrownBy(() -> WarehouseStatus.of(invalidInput))
				.isInstanceOf(ValidationException.class)
				.hasMessageContaining(WarehouseErrorCode.WAREHOUSE_STATUS_INVALID.getDescr());
	}

	@Test
	void constructor_WithValidInput_ThenCreateInstance() {
		String validInput = "ACTIVE";
		WarehouseStatus warehouseStatus = WarehouseStatus.of(validInput);
		
		assertThat(warehouseStatus).isNotNull();
		assertThat(warehouseStatus.code()).isEqualTo(validInput);
	}
	
	@Test
	void toString_ShouldReturnCode() {
		String validInput = "ACTIVE";
		WarehouseStatus warehouseStatus = WarehouseStatus.of(validInput);
		
		assertThat(warehouseStatus).hasToString(validInput);
	}
}
