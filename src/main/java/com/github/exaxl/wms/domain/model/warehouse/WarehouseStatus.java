package com.github.exaxl.wms.domain.model.warehouse;

import org.apache.commons.lang3.StringUtils;

import com.github.exaxl.error.exception.core.ValidationException;
import com.github.exaxl.wms.domain.enums.WarehouseErrorCode;

public record WarehouseStatus(String code) {

    public static final WarehouseStatus ACTIVE = WarehouseStatus.of("ACTIVE");
	
	public WarehouseStatus {
		if (StringUtils.isBlank(code)) {
			throw new ValidationException(WarehouseErrorCode.WAREHOUSE_STATUS_INVALID);
		}
	}

	public static WarehouseStatus of(String code) {
		return new WarehouseStatus(code);
	}
	
	@Override
    public String toString() {
        return code;
    }
}
