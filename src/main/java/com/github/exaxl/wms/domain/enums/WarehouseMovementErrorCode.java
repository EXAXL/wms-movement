package com.github.exaxl.wms.domain.enums;

import com.github.exaxl.error.exception.core.ErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum WarehouseMovementErrorCode implements ErrorCode {

	ADJUSTMENT_REFERENCE_NOT_ALLOWED("WM_0001", "Adjustment movement cannot have reference"),
	MISSING_REFERENCE("WM_0002", "Reference is required for this movement type"),
	MISSING_REQUIRED_FIELD("WM_0003", "Required field is missing"),
	INVALID_VALUE("WM_0004", "Invalid value provided");
	
	private String code;
	private String descr;
}
