package com.github.exaxl.wms.domain.service;

import org.springframework.stereotype.Service;

import com.github.exaxl.wms.adapter.out.db.entity.MovementNaturalId;
import com.github.exaxl.wms.adapter.out.db.repository.MockedMovementRepository;
import com.github.exaxl.wms.domain.exception.DuplicateMovementException;
import com.github.exaxl.wms.domain.model.movement.WarehouseMovement;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MovementService {

	private final MockedMovementRepository mockedMovementRepository;

	public MovementService(MockedMovementRepository mockedMovementRepository) {
		this.mockedMovementRepository = mockedMovementRepository;
	}

	public void ensureNotDuplicate(String movementId, int whCode) {
		boolean exists = mockedMovementRepository
				.existsByMovementIdAndWarehouseCode(new MovementNaturalId(movementId, whCode));
		if (exists) {
			String errorMessage = String.format("Duplicate movement detected - movementId: %s, warehouseCode: %s",
					movementId, whCode);
			log.warn(errorMessage);
			throw new DuplicateMovementException(errorMessage);
		}
	}
	
	public void registerMovement(WarehouseMovement movement) {
        // TODO mettere FUORI mapping con la entity e salvare su db
		mockedMovementRepository.save(movement);
    }
}
