package com.github.exaxl.wms.domain.model.warehouse;

import org.apache.commons.lang3.StringUtils;

import com.github.exaxl.error.exception.core.ValidationException;
import com.github.exaxl.wms.domain.enums.BinErrorCode;

public record BinStatus(String code) {

	public static final BinStatus AVAILABLE = new BinStatus("AVAILABLE");
	
	public BinStatus {
		if (StringUtils.isBlank(code)) {
			throw new ValidationException(BinErrorCode.BIN_STATUS_INVALID);
		}
	}

	public static BinStatus of(String code) {
		return new BinStatus(code);
	}
	
	@Override
    public String toString() {
        return code;
    }
}
