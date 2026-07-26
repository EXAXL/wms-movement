package com.github.exaxl.wms.adapter.in.messaging.enums;

import com.github.exaxl.error.exception.core.ErrorCode;
import com.github.exaxl.wms.common.ErrorCodePrefix;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MessageErrorCode implements ErrorCode {

	PARSING_FAILED(ErrorCodePrefix.SERVICE + "-MESSAGE-0001", "Failed to parse message"),
	MISSING_HEADER(ErrorCodePrefix.SERVICE + "-MESSAGE-0002", "Missing required header in message"),
	MESSAGE_VALIDATION_FAILED(ErrorCodePrefix.SERVICE + "-MESSAGE-0003", "Invalid message received"),
	GENERIC_ERROR(ErrorCodePrefix.SERVICE + "-MESSAGE-9999", "Unknown error");

	private final String code;
	private final String descr;
}
