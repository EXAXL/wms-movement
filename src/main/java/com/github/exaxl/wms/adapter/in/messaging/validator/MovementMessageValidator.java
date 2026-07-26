package com.github.exaxl.wms.adapter.in.messaging.validator;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.github.exaxl.wms.adapter.in.messaging.exception.MessageValidationException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

@Component
public class MovementMessageValidator {

	private static final String COLON_SEPARATOR = ": ";
	private static final String SEMICOLONS_DELIMITER = "; ";

	private final Validator validator;

	public MovementMessageValidator(Validator validator) {
		this.validator = validator;
	}

	public void validate(Object message) {
		Set<ConstraintViolation<Object>> violations = validator.validate(message);
		if (!violations.isEmpty()) {
			throw new MessageValidationException(
					violations.stream().map(v -> v.getPropertyPath() + COLON_SEPARATOR + v.getMessage())
							.collect(Collectors.joining(SEMICOLONS_DELIMITER)));
		}
	}
}
