package com.github.exaxl.wms.adapter.out.db.repository;

import java.time.ZoneId;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.github.exaxl.wms.domain.model.warehouse.WarehouseStatus;

@Component
public class MockedWarehouseRepository {

	public Optional<WarehouseValidationProjection> findByCode(int code) {
		// TODO QUERY PER VEDERE SE IL WAREHOUSE ESISTE, SE NON ESISTE RITORNA Optional.empty()
		return Optional.of(new WarehouseValidationProjection(code, WarehouseStatus.of("ACTIVE"), ZoneId.of("UTC")));
	}
	
	public record WarehouseValidationProjection(int code, WarehouseStatus status, ZoneId timezone) {}
}
