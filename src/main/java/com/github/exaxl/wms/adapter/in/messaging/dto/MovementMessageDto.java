package com.github.exaxl.wms.adapter.in.messaging.dto;

import java.time.Instant;

import com.github.exaxl.wms.domain.model.movement.MovementType;
import com.github.exaxl.wms.domain.model.movement.ReferenceType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

// @formatter:off
public record MovementMessageDto(
		@NotBlank(message = "movementId must not be blank") String movementId, 
		@Positive(message = "warehouseCode must be greater than 0") int warehouseCode, 
		@NotNull(message = "movementType must not be null") MovementType movementType,
		@NotBlank(message = "operatorCode must not be blank") String operatorCode, 
		@NotNull(message = "movementCreatedAt must not be null") Instant movementCreatedAt, 
		String referenceId, 
		ReferenceType referenceType,
		@NotBlank(message = "productSku must not be blank") String productSku, 
		@Positive(message = "qty must be greater than 0") int qty, 
		String binCode, 
		String lotCode) {
}
// @formatter:on