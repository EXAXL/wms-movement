package com.github.exaxl.wms.domain.usecase.registermovement;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.github.exaxl.wms.adapter.in.messaging.dto.MovementMessageDto;
import com.github.exaxl.wms.domain.model.movement.WarehouseMovement;
import com.github.exaxl.wms.domain.service.BinService;
import com.github.exaxl.wms.domain.service.BinService.BinValidationResult;
import com.github.exaxl.wms.domain.service.MovementService;
import com.github.exaxl.wms.domain.service.ProductService;
import com.github.exaxl.wms.domain.service.ProductService.ProductValidationResult;
import com.github.exaxl.wms.domain.service.WarehouseService;

@Service
public class RegisterMovement {

	private final MovementService movementService;
	private final WarehouseService warehouseService;
	private final ProductService productService;
	private final BinService binService;
	private final MovementPersistenceManager movementPersistenceManager;

	public RegisterMovement(MovementService movementService, WarehouseService warehouseService,
			ProductService productService, BinService binService,
			MovementPersistenceManager movementPersistenceManager) {
		this.movementService = movementService;
		this.warehouseService = warehouseService;
		this.productService = productService;
		this.binService = binService;
		this.movementPersistenceManager = movementPersistenceManager;
	}

	public void register(MovementMessageDto movementMessageDto) {

		movementService.ensureNotDuplicate(movementMessageDto.movementId(), movementMessageDto.warehouseCode());
		warehouseService.requireActiveWarehouse(movementMessageDto.warehouseCode());

		ProductValidationResult productValidation = productService
				.requireAllowedProduct(movementMessageDto.productSku(), movementMessageDto.movementType());
		Optional<BinValidationResult> binValidation = binService.validateBin(movementMessageDto.binCode(),
				movementMessageDto.warehouseCode(), productValidation.product().attributes());

		WarehouseMovement movement = MovementMessageMapper.buildMovement(movementMessageDto, binValidation);
		movementPersistenceManager.registerMovementAndOutboxEvent(movement);
	}

}
