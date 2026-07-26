package com.github.exaxl.wms.domain.exception;

import com.github.exaxl.error.exception.core.NotFoundException;
import com.github.exaxl.wms.domain.enums.WarehouseErrorCode;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@SuppressWarnings("java:S110")
public class WarehouseNotFoundException extends NotFoundException{

	private static final long serialVersionUID = 4256058946431405403L;

	public WarehouseNotFoundException() {
        super(WarehouseErrorCode.WAREHOUSE_NOT_FOUND);
    }
	
	public WarehouseNotFoundException(@NotNull Throwable cause) {
		super(WarehouseErrorCode.WAREHOUSE_NOT_FOUND, cause);
	}

	public WarehouseNotFoundException(@NotEmpty String message) {
		super(message, WarehouseErrorCode.WAREHOUSE_NOT_FOUND);
	}

	public WarehouseNotFoundException(@NotEmpty String message, @NotNull Throwable cause) {
		super(message, WarehouseErrorCode.WAREHOUSE_NOT_FOUND, cause);
	}
}
