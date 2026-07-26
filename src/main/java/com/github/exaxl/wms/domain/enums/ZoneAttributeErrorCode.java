package com.github.exaxl.wms.domain.enums;

import com.github.exaxl.error.exception.core.ErrorCode;
import com.github.exaxl.wms.common.ErrorCodePrefix;

import lombok.AllArgsConstructor;
import lombok.Generated;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Generated
public enum ZoneAttributeErrorCode implements ErrorCode{

	ZONE_ATTRIBUTE_INVALID(ErrorCodePrefix.SERVICE + "-ZONE-ATTRIBUTE-0001", "Zone has invalid attribute");
	
	private final String code;
	private final String descr;
}
