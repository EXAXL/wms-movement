package com.github.exaxl.wms.adapter.out.db.repository;

import org.springframework.stereotype.Component;

import com.github.exaxl.wms.adapter.out.db.entity.MovementNaturalId;

@Component
public class MockedMovementRepository {

	public boolean existsByMovementIdAndWarehouseCode(MovementNaturalId movementNaturalId) {
		return false;
	}
	
	public void save(Object movement) {
		// Mocked save operation
	}
}
