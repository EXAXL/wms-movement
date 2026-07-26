package com.github.exaxl.wms.domain.usecase.registermovement;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import com.github.exaxl.wms.adapter.in.messaging.dto.MovementMessageDto;
import com.github.exaxl.wms.adapter.out.db.repository.MockedBinRepository.BinValidationProjection;
import com.github.exaxl.wms.domain.model.movement.MovementStatus;
import com.github.exaxl.wms.domain.model.movement.MovementType;
import com.github.exaxl.wms.domain.model.movement.ReferenceType;
import com.github.exaxl.wms.domain.model.movement.WarehouseMovement;
import com.github.exaxl.wms.domain.model.movement.WarehouseMovement.MovementReference;
import com.github.exaxl.wms.domain.model.warehouse.BinStatus;
import com.github.exaxl.wms.domain.service.BinService.BinValidationResult;

class MovementMessageMapperTest {

	@Test
	void buildReference_WhenTypeCannotHaveReference_ThenReturnNull() {
		MovementMessageDto dto = new MovementMessageDto("movementId", 1, MovementType.ADJUSTMENT, "operatorCode",
				Instant.now(), null, null, "productSku", 10, "binCode", "lotCode");

		assertThat(MovementMessageMapper.buildReference(dto)).isNull();
	}

	@ParameterizedTest
	@EnumSource(value = MovementType.class, names = { "ADJUSTMENT" }, mode = EnumSource.Mode.EXCLUDE)
	void buildReference_WhenTypeCanHaveReference_ThenReturnReference(MovementType type) {
		MovementMessageDto dto = new MovementMessageDto("movementId", 1, type, "operatorCode", Instant.now(),
				"referenceId", ReferenceType.PURCHASE_ORDER, "productSku", 10, "binCode", "lotCode");

		MovementReference reference = MovementMessageMapper.buildReference(dto);

		assertThat(reference).isNotNull();
		assertThat(reference.referenceId()).isEqualTo("referenceId");
		assertThat(reference.referenceType()).isEqualTo(ReferenceType.PURCHASE_ORDER);
	}

	@Test
	void buildMovement_WhenBinHasWarning_ThenStatusIsReceivedWithWarnings() {
		MovementMessageDto dto = new MovementMessageDto("movementId", 1, MovementType.INBOUND, "operatorCode",
				Instant.now(), "referenceId", ReferenceType.PURCHASE_ORDER, "productSku", 10, "binCode", "lotCode");

		BinValidationProjection binProjection = new BinValidationProjection("binCode", 1, BinStatus.of("AVAILABLE"), Set.of());
		Optional<BinValidationResult> binResult = Optional
				.of(new BinValidationResult(binProjection, true, Optional.of("Bin status is not AVAILABLE")));

		WarehouseMovement movement = MovementMessageMapper.buildMovement(dto, binResult);

		assertThat(movement.getStatus())
				.isEqualTo(MovementStatus.RECEIVED_WITH_WARNINGS);
	}
	
	@Test
	void buildMovement_WhenBinHasNoWarning_ThenStatusIsReceived() {
		MovementMessageDto dto = new MovementMessageDto("movementId", 1, MovementType.INBOUND, "operatorCode",
				Instant.now(), "referenceId", ReferenceType.PURCHASE_ORDER, "productSku", 10, "binCode", "lotCode");

		BinValidationProjection binProjection = new BinValidationProjection("binCode", 1, BinStatus.of("AVAILABLE"), Set.of());
		Optional<BinValidationResult> binResult = Optional
				.of(new BinValidationResult(binProjection, false, Optional.empty()));

		WarehouseMovement movement = MovementMessageMapper.buildMovement(dto, binResult);

		assertThat(movement.getStatus())
				.isEqualTo(MovementStatus.RECEIVED);
	}
	
	@Test
	void buildMovement_WhenBinResultIsEmpty_ThenStatusIsReceived() {
		MovementMessageDto dto = new MovementMessageDto("movementId", 1, MovementType.INBOUND, "operatorCode",
				Instant.now(), "referenceId", ReferenceType.PURCHASE_ORDER, "productSku", 10, "binCode", "lotCode");

		Optional<BinValidationResult> binResult = Optional.empty();

		WarehouseMovement movement = MovementMessageMapper.buildMovement(dto, binResult);

		assertThat(movement.getStatus())
				.isEqualTo(MovementStatus.RECEIVED);
		assertThat(movement.binCode()).isEmpty();	
	}
	
	@Test
	void buildMovement() {
		MovementMessageDto dto = new MovementMessageDto("movementId", 1, MovementType.INBOUND, "operatorCode",
				Instant.now(), "referenceId", ReferenceType.PURCHASE_ORDER, "productSku", 10, "binCode", "lotCode");

		Optional<BinValidationResult> binResult = Optional.empty();

		WarehouseMovement movement = MovementMessageMapper.buildMovement(dto, binResult);

		assertThat(movement.getMovementId()).isEqualTo("movementId");
		assertThat(movement.getWhCode()).isEqualTo(1);
		assertThat(movement.getProductSku()).isEqualTo("productSku");
		assertThat(movement.getType()).isEqualTo(MovementType.INBOUND);
		assertThat(movement.getQty()).isEqualTo(10);
		assertThat(movement.getOperatorCode()).isEqualTo("operatorCode");
		assertThat(movement.lotCode()).contains("lotCode");
		assertThat(movement.getMovementCreatedAt()).isNotNull();
		assertThat(movement.getReceivedAt()).isNotNull();
		assertThat(movement.getStatus()).isEqualTo(MovementStatus.RECEIVED);
		assertThat(movement.reference()).isNotEmpty().get().satisfies(ref -> {
			assertThat(ref.referenceId()).isEqualTo("referenceId");
			assertThat(ref.referenceType()).isEqualTo(ReferenceType.PURCHASE_ORDER);
		});
		assertThat(movement.binCode()).isEmpty();
	}
}
