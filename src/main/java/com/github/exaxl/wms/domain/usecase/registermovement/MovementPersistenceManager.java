package com.github.exaxl.wms.domain.usecase.registermovement;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.exaxl.wms.domain.model.movement.WarehouseMovement;
import com.github.exaxl.wms.domain.service.MovementService;
import com.github.exaxl.wms.domain.service.OutboxService;

@Service
public class MovementPersistenceManager {

	private final MovementService movementService;
	private final OutboxService outboxService;
	
	public MovementPersistenceManager(MovementService movementService, OutboxService outboxService) {
		this.movementService = movementService;
		this.outboxService = outboxService;
	}
	
	@Transactional
	public void registerMovementAndOutboxEvent(WarehouseMovement movement) {
		movementService.registerMovement(movement);
		outboxService.saveOutboxEvent(movement);
	}
}
