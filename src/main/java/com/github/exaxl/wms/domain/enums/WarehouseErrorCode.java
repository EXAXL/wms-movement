package com.github.exaxl.wms.domain.enums;

import com.github.exaxl.error.exception.core.ErrorCode;
import com.github.exaxl.wms.common.ErrorCodePrefix;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum WarehouseErrorCode implements ErrorCode {

	WAREHOUSE_NOT_FOUND(ErrorCodePrefix.SERVICE + "-WAREHOUSE-0001", "Warehouse not found"),
	WAREHOUSE_NOT_ACTIVE(ErrorCodePrefix.SERVICE + "-WAREHOUSE-0002", "Warehouse is not active"),
	WAREHOUSE_STATUS_INVALID(ErrorCodePrefix.SERVICE + "-WAREHOUSE-0003", "Warehouse status is invalid");

	private final String code;
	private final String descr;
}
