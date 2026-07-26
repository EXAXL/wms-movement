package com.github.exaxl.wms.domain.enums;

import com.github.exaxl.error.exception.core.ErrorCode;
import com.github.exaxl.wms.common.ErrorCodePrefix;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProductErrorCode implements ErrorCode {

	PRODUCT_NOT_FOUND(ErrorCodePrefix.SERVICE + "-PRODUCT-0001", "Product not found"),
	PRODUCT_NOT_ALLOWED(ErrorCodePrefix.SERVICE + "-PRODUCT-0002", "Product not allowed for this movement type");

	private final String code;
	private final String descr;
}
