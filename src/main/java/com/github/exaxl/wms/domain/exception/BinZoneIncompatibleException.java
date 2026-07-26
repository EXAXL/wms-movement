package com.github.exaxl.wms.domain.exception;

import com.github.exaxl.error.exception.core.ValidationException;
import com.github.exaxl.wms.domain.enums.BinErrorCode;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@SuppressWarnings("java:S110")
public class BinZoneIncompatibleException extends ValidationException{

	private static final long serialVersionUID = 1626112011295787609L;

	public BinZoneIncompatibleException() {
		super(BinErrorCode.BIN_ZONE_INCOMPATIBLE);
	}

	public BinZoneIncompatibleException(@NotNull Throwable cause) {
		super(BinErrorCode.BIN_ZONE_INCOMPATIBLE, cause);
	}

	public BinZoneIncompatibleException(@NotEmpty String message) {
		super(message, BinErrorCode.BIN_ZONE_INCOMPATIBLE);
	}

	public BinZoneIncompatibleException(@NotEmpty String message, @NotNull Throwable cause) {
		super(message, BinErrorCode.BIN_ZONE_INCOMPATIBLE, cause);
	}
}
