package com.github.exaxl.wms.domain.enums;

import com.github.exaxl.error.exception.core.ErrorCode;
import com.github.exaxl.wms.common.ErrorCodePrefix;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MovementErrorCode implements ErrorCode {

	MOVEMENT_DUPLICATED(ErrorCodePrefix.SERVICE + "-MOVEMENT-0001", "Duplicate movement detected");
	
	private final String code;
	private final String descr;
}
