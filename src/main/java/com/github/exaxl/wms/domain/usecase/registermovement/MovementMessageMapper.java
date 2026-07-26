package com.github.exaxl.wms.domain.usecase.registermovement;

import java.time.Instant;
import java.util.Optional;

import com.github.exaxl.wms.adapter.in.messaging.dto.MovementMessageDto;
import com.github.exaxl.wms.domain.model.movement.MovementRules;
import com.github.exaxl.wms.domain.model.movement.MovementStatus;
import com.github.exaxl.wms.domain.model.movement.WarehouseMovement;
import com.github.exaxl.wms.domain.model.movement.WarehouseMovement.MovementReference;
import com.github.exaxl.wms.domain.service.BinService.BinValidationResult;

public final class MovementMessageMapper {

	public static WarehouseMovement buildMovement(MovementMessageDto dto, Optional<BinValidationResult> binResult) {

		boolean hasWarning = binResult.map(BinValidationResult::hasWarning).orElse(false);

		MovementStatus status = hasWarning ? MovementStatus.RECEIVED_WITH_WARNINGS : MovementStatus.RECEIVED;

		return WarehouseMovement.builder().movementId(dto.movementId()).whCode(dto.warehouseCode())
				.productSku(dto.productSku()).type(dto.movementType()).qty(dto.qty()).operatorCode(dto.operatorCode())
				.lotCode(dto.lotCode()).movementCreatedAt(dto.movementCreatedAt()).receivedAt(Instant.now())
				.status(status).reference(buildReference(dto))
				.binCode(binResult.map(r -> r.bin().binCode()).orElse(null)).build();
	}

	protected static MovementReference buildReference(MovementMessageDto dto) {
		return MovementRules.forbidsReference(dto.movementType()) ? null
				: new MovementReference(dto.referenceId(), dto.referenceType());
	}

	private MovementMessageMapper() {
	}
}
