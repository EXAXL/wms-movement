package com.github.exaxl.wms.domain.model.product;

import org.apache.commons.lang3.StringUtils;

import com.github.exaxl.error.exception.core.ValidationException;
import com.github.exaxl.wms.domain.enums.ProductStatusErrorCode;

public record ProductStatus(String code) {

	public static final ProductStatus DISCONTINUED = new ProductStatus("DISCONTINUED");
	
	public static ProductStatus of(String code) {
		return new ProductStatus(code);
	}

	public ProductStatus {
		if (StringUtils.isBlank(code)) {
			throw new ValidationException(ProductStatusErrorCode.PRODUCT_STATUS_INVALID);
		}
	}
	
	@Override
    public String toString() {
        return code;
    }
}
