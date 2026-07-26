package com.github.exaxl.wms.domain.enums;

import com.github.exaxl.error.exception.core.ErrorCode;
import com.github.exaxl.wms.common.ErrorCodePrefix;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BinErrorCode implements ErrorCode{

	BIN_NOT_FOUND(ErrorCodePrefix.SERVICE + "-BIN-0001", "Bin not found"),
	BIN_ZONE_INCOMPATIBLE(ErrorCodePrefix.SERVICE + "-BIN-0002", "Bin zone incompatible with product attributes"),
	BIN_STATUS_INVALID(ErrorCodePrefix.SERVICE + "-BIN-0003", "Bin status is invalid");
	
	private final String code;
	private final String descr;
}
