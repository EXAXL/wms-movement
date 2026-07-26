package com.github.exaxl.wms.domain.model.warehouse;

import org.apache.commons.lang3.StringUtils;

import com.github.exaxl.error.exception.core.ValidationException;
import com.github.exaxl.wms.domain.enums.ZoneAttributeErrorCode;

public record ZoneAttribute(String code) {

	public ZoneAttribute {
		if (StringUtils.isBlank(code)) {
			throw new ValidationException(ZoneAttributeErrorCode.ZONE_ATTRIBUTE_INVALID);
		}
	}

	public static ZoneAttribute of(String code) {
		return new ZoneAttribute(code);
	}
	
	@Override
    public String toString() {
        return code;
    }
}
