package com.github.exaxl.wms.domain.exception;

import com.github.exaxl.error.exception.core.NotFoundException;
import com.github.exaxl.wms.domain.enums.BinErrorCode;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@SuppressWarnings("java:S110")
public class BinNotFoundException extends NotFoundException {

	private static final long serialVersionUID = -3368137595745981916L;

	public BinNotFoundException() {
		super(BinErrorCode.BIN_NOT_FOUND);
	}

	public BinNotFoundException(@NotNull Throwable cause) {
		super(BinErrorCode.BIN_NOT_FOUND, cause);
	}

	public BinNotFoundException(@NotEmpty String message) {
		super(message, BinErrorCode.BIN_NOT_FOUND);
	}

	public BinNotFoundException(@NotEmpty String message, @NotNull Throwable cause) {
		super(message, BinErrorCode.BIN_NOT_FOUND, cause);
	}
}
