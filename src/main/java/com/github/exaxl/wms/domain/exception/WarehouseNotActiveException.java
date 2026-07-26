package com.github.exaxl.wms.domain.exception;

import com.github.exaxl.error.exception.core.ValidationException;
import com.github.exaxl.wms.domain.enums.WarehouseErrorCode;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@SuppressWarnings("java:S110")
public class WarehouseNotActiveException extends ValidationException implements RetryableError {

	private static final long serialVersionUID = 5753438148609419495L;

	public WarehouseNotActiveException() {
        super(WarehouseErrorCode.WAREHOUSE_NOT_ACTIVE);
    }
	
	public WarehouseNotActiveException(@NotNull Throwable cause) {
		super(WarehouseErrorCode.WAREHOUSE_NOT_ACTIVE, cause);
	}

	public WarehouseNotActiveException(@NotEmpty String message) {
		super(message, WarehouseErrorCode.WAREHOUSE_NOT_ACTIVE);
	}

	public WarehouseNotActiveException(@NotEmpty String message, @NotNull Throwable cause) {
		super(message, WarehouseErrorCode.WAREHOUSE_NOT_ACTIVE, cause);
	}
}
