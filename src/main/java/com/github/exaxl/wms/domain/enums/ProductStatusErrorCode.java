package com.github.exaxl.wms.domain.enums;

import com.github.exaxl.error.exception.core.ErrorCode;
import com.github.exaxl.wms.common.ErrorCodePrefix;

import lombok.AllArgsConstructor;
import lombok.Generated;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Generated
public enum ProductStatusErrorCode implements ErrorCode {

	PRODUCT_STATUS_INVALID(ErrorCodePrefix.SERVICE + "-PRODUCT-STATUS-0001", "Product status is invalid");

	private final String code;
	private final String descr;

}
