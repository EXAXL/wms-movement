package com.github.exaxl.wms.domain.exception;

import com.github.exaxl.error.exception.core.ValidationException;
import com.github.exaxl.wms.domain.enums.MovementErrorCode;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@SuppressWarnings("java:S110")
public class DuplicateMovementException extends ValidationException{

	private static final long serialVersionUID = 671557067975788186L;

	public DuplicateMovementException() {
        super(MovementErrorCode.MOVEMENT_DUPLICATED);
    }
	
	public DuplicateMovementException(@NotNull Throwable cause) {
		super(MovementErrorCode.MOVEMENT_DUPLICATED, cause);
	}

	public DuplicateMovementException(@NotEmpty String message) {
		super(message, MovementErrorCode.MOVEMENT_DUPLICATED);
	}

	public DuplicateMovementException(@NotEmpty String message, @NotNull Throwable cause) {
		super(message, MovementErrorCode.MOVEMENT_DUPLICATED, cause);
	}
}
