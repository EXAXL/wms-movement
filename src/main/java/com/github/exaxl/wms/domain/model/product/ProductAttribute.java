package com.github.exaxl.wms.domain.model.product;

import org.apache.commons.lang3.StringUtils;

import com.github.exaxl.error.exception.core.ValidationException;
import com.github.exaxl.wms.domain.enums.ProductAttributeErrorCode;

public record ProductAttribute(String code) {

	public static ProductAttribute of(String code) {
		return new ProductAttribute(code);
	}

	public ProductAttribute {
		if (StringUtils.isBlank(code)) {
			throw new ValidationException(ProductAttributeErrorCode.PRODUCT_ATTRIBUTE_INVALID);
		}
	}
	
	@Override
    public String toString() {
        return code;
    }
}
