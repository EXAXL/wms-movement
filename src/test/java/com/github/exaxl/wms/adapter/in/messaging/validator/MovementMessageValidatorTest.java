package com.github.exaxl.wms.adapter.in.messaging.validator;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.exaxl.wms.adapter.in.messaging.dto.MovementMessageDto;
import com.github.exaxl.wms.adapter.in.messaging.exception.MessageValidationException;

import jakarta.validation.Validation;

class MovementMessageValidatorTest {

	private MovementMessageValidator validator;

	@BeforeEach
	void setUp() {
		validator = new MovementMessageValidator(Validation.buildDefaultValidatorFactory().getValidator());
	}

	@Test
	void validate_WhenMessageIsValid_ThenNoExceptionIsThrown() {
		MovementMessageDto message = Instancio.create(MovementMessageDto.class);

		assertThatNoException().isThrownBy(() -> validator.validate(message));
	}

	@Test
	void validate_WhenQtyIsNegative_ThenThrowsMessageValidationException() {
		MovementMessageDto message = Instancio.of(MovementMessageDto.class)
				.set(Select.field(MovementMessageDto::qty), -1).create();

		assertThatThrownBy(() -> validator.validate(message)).isInstanceOf(MessageValidationException.class)
				.hasMessageContaining("qty");
	}

	@Test
	void validate_WhenMultipleFieldsAreInvalid_ThenExceptionMessageContainsAllViolations() {
		MovementMessageDto message = Instancio.of(MovementMessageDto.class)
				.set(Select.field(MovementMessageDto::qty), -1).set(Select.field(MovementMessageDto::movementId), null)
				.create();

		assertThatThrownBy(() -> validator.validate(message)).isInstanceOf(MessageValidationException.class)
				.hasMessageContaining("movementId").hasMessageContaining("qty");
	}
}
