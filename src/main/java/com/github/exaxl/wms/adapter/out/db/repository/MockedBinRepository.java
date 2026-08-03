package com.github.exaxl.wms.adapter.out.db.repository;

import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.github.exaxl.wms.domain.model.warehouse.BinStatus;
import com.github.exaxl.wms.domain.model.warehouse.ZoneAttribute;

@Component
public class MockedBinRepository {

	public Optional<BinValidationProjection> findByCodeAndWarehouseCode(String binCode, int warehouseCode) {
		return Optional.ofNullable(new BinValidationProjection(binCode, warehouseCode, BinStatus.of("AVAILABLE"),
				Set.of(ZoneAttribute.of("HAZARDOUS"), ZoneAttribute.of("REFRIGERATED"))));
	}

	public record BinValidationProjection(String binCode, int warehouseCode, BinStatus status,
			Set<ZoneAttribute> zoneAttributes) {
	}
}
