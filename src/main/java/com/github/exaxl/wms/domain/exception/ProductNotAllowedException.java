package com.github.exaxl.wms.domain.exception;

import com.github.exaxl.error.exception.core.ValidationException;
import com.github.exaxl.wms.domain.enums.ProductErrorCode;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@SuppressWarnings("java:S110")
public class ProductNotAllowedException extends ValidationException{

	private static final long serialVersionUID = -7064143772960193704L;

	public ProductNotAllowedException() {
        super(ProductErrorCode.PRODUCT_NOT_ALLOWED);
    }
	
	public ProductNotAllowedException(@NotNull Throwable cause) {
		super(ProductErrorCode.PRODUCT_NOT_ALLOWED, cause);
	}

	public ProductNotAllowedException(@NotEmpty String message) {
		super(message, ProductErrorCode.PRODUCT_NOT_ALLOWED);
	}

	public ProductNotAllowedException(@NotEmpty String message, @NotNull Throwable cause) {
		super(message, ProductErrorCode.PRODUCT_NOT_ALLOWED, cause);
	}
}
