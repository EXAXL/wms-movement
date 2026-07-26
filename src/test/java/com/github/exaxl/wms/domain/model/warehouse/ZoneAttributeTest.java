package com.github.exaxl.wms.domain.model.warehouse;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import com.github.exaxl.error.exception.core.ValidationException;
import com.github.exaxl.wms.domain.enums.ZoneAttributeErrorCode;

class ZoneAttributeTest {

	@ParameterizedTest
	@NullAndEmptySource
	void constructor_WithInvalidInput_ThenThrowException(String invalidInput) {
		Assertions.assertThatThrownBy(() -> ZoneAttribute.of(invalidInput))
				.isInstanceOf(ValidationException.class)
				.hasMessageContaining(ZoneAttributeErrorCode.ZONE_ATTRIBUTE_INVALID.getDescr());
	}
	
	@Test
	void constructor_WithValidInput_ThenCreateInstance() {
		String validInput = "TEMPERATURE_CONTROLLED";
		ZoneAttribute zoneAttribute = ZoneAttribute.of(validInput);
		
		assertThat(zoneAttribute).isNotNull();
		assertThat(zoneAttribute.code()).isEqualTo(validInput);
	}

	@Test
	void toString_ShouldReturnCode() {
		String validInput = "TEMPERATURE_CONTROLLED";
		ZoneAttribute zoneAttribute = ZoneAttribute.of(validInput);
		
		assertThat(zoneAttribute).hasToString(validInput);
	}
}
