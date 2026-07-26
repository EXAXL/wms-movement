package com.github.exaxl.wms.domain.model.product;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import com.github.exaxl.error.exception.core.ValidationException;
import com.github.exaxl.wms.domain.enums.ProductStatusErrorCode;

class ProductStatusTest {

	@ParameterizedTest
	@NullAndEmptySource
	void constructor_WithInvalidInput_ThenThrowException(String invalidInput) {
		
		Assertions.assertThatThrownBy(() -> ProductStatus.of(invalidInput))
				.isInstanceOf(ValidationException.class)
				.hasMessageContaining(ProductStatusErrorCode.PRODUCT_STATUS_INVALID.getDescr());
	}


	@Test
	void constructor_WithValidInput_ThenCreateInstance() {
		String validInput = "VALID_STATUS";
		ProductStatus productStatus = ProductStatus.of(validInput);
		
		Assertions.assertThat(productStatus).isNotNull();
		Assertions.assertThat(productStatus.code()).isEqualTo(validInput);
	}
	
	@Test
	void toString_ShouldReturnCode() {
		String validInput = "VALID_STATUS";
		ProductStatus productStatus = ProductStatus.of(validInput);
		
		Assertions.assertThat(productStatus).hasToString(validInput);
	}
}
