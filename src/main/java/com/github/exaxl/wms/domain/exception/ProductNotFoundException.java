package com.github.exaxl.wms.domain.exception;

import com.github.exaxl.error.exception.core.NotFoundException;
import com.github.exaxl.wms.domain.enums.ProductErrorCode;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Generated;

@SuppressWarnings("java:S110")
@Generated
public class ProductNotFoundException extends NotFoundException{
	
	private static final long serialVersionUID = -5128490943895146282L;

	public ProductNotFoundException() {
        super(ProductErrorCode.PRODUCT_NOT_FOUND);
    }
	
	public ProductNotFoundException(@NotNull Throwable cause) {
		super(ProductErrorCode.PRODUCT_NOT_FOUND, cause);
	}

	public ProductNotFoundException(@NotEmpty String message) {
		super(message, ProductErrorCode.PRODUCT_NOT_FOUND);
	}

	public ProductNotFoundException(@NotEmpty String message, @NotNull Throwable cause) {
		super(message, ProductErrorCode.PRODUCT_NOT_FOUND, cause);
	}
	
}
