package com.github.exaxl.wms.domain.model.movement;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;

import org.assertj.core.api.Assertions;
import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import com.github.exaxl.error.exception.core.ValidationException;
import com.github.exaxl.wms.domain.enums.WarehouseMovementErrorCode;
import com.github.exaxl.wms.domain.model.movement.WarehouseMovement.MovementReference;
import com.github.exaxl.wms.domain.model.movement.WarehouseMovement.WarehouseMovementBuilder;

class WarehouseMovementTest {

	@Nested
	class EqualsAndHashCode {
		@Test
		void equals_WhenMovementIdAndWhCodeAreEquals_ThenTwoObjectAreEqual() {
			WarehouseMovement movement1 = Instancio.of(WarehouseMovement.class)
					.set(Select.field(WarehouseMovement::getMovementId), "movementId")
					.set(Select.field(WarehouseMovement::getWhCode), 1)
					.set(Select.field(WarehouseMovement::getType), MovementType.INBOUND).create();

			WarehouseMovement movement2 = Instancio.of(WarehouseMovement.class)
					.set(Select.field(WarehouseMovement::getMovementId), "movementId")
					.set(Select.field(WarehouseMovement::getWhCode), 1)
					.set(Select.field(WarehouseMovement::getType), MovementType.INBOUND).create();

			assertThat(movement1).isEqualTo(movement2);
		}

		@Test
		void equals_WhenMovementIdAreDifferent_ThenTwoObjectAreNotEqual() {
			WarehouseMovement movement1 = Instancio.of(WarehouseMovement.class)
					.set(Select.field(WarehouseMovement::getMovementId), "movementId1")
					.set(Select.field(WarehouseMovement::getWhCode), 1)
					.set(Select.field(WarehouseMovement::getType), MovementType.INBOUND).create();

			WarehouseMovement movement2 = Instancio.of(WarehouseMovement.class)
					.set(Select.field(WarehouseMovement::getMovementId), "movementId2")
					.set(Select.field(WarehouseMovement::getWhCode), 1)
					.set(Select.field(WarehouseMovement::getType), MovementType.INBOUND).create();

			assertThat(movement1).isNotEqualTo(movement2);
		}

		@Test
		void equals_WhenMovementIdIsSameAndWhCodeIsDifferent_ThenTwoObjectAreNotEqual() {
			WarehouseMovement movement1 = Instancio.of(WarehouseMovement.class)
					.set(Select.field(WarehouseMovement::getMovementId), "movementId")
					.set(Select.field(WarehouseMovement::getWhCode), 1)
					.set(Select.field(WarehouseMovement::getType), MovementType.INBOUND).create();

			WarehouseMovement movement2 = Instancio.of(WarehouseMovement.class)
					.set(Select.field(WarehouseMovement::getMovementId), "movementId")
					.set(Select.field(WarehouseMovement::getWhCode), 2)
					.set(Select.field(WarehouseMovement::getType), MovementType.INBOUND).create();

			assertThat(movement1).isNotEqualTo(movement2);
		}

		@Test
		void hashCode_WhenMovementIdAndWhCodeAreEquals_ThenTwoObjectHaveSameHashCode() {
			WarehouseMovement movement1 = Instancio.of(WarehouseMovement.class)
					.set(Select.field(WarehouseMovement::getMovementId), "movementId")
					.set(Select.field(WarehouseMovement::getWhCode), 1)
					.set(Select.field(WarehouseMovement::getType), MovementType.INBOUND).create();

			WarehouseMovement movement2 = Instancio.of(WarehouseMovement.class)
					.set(Select.field(WarehouseMovement::getMovementId), "movementId")
					.set(Select.field(WarehouseMovement::getWhCode), 1)
					.set(Select.field(WarehouseMovement::getType), MovementType.INBOUND).create();

			assertThat(movement1).hasSameHashCodeAs(movement2);
		}
	}

	@Nested
	class OptionalFields {

		@Test
		void binCode_WhenBinCodeIsNull_ThenReturnEmptyOptional() {
			WarehouseMovement movement = Instancio.of(WarehouseMovement.class)
					.set(Select.field(WarehouseMovement::binCode), null).create();

			assertThat(movement.binCode()).isEmpty();
		}

		@Test
		void binCode_WhenBinCodeIsNotNull_ThenReturnOptionalWithValue() {
			WarehouseMovement movement = Instancio.of(WarehouseMovement.class)
					.set(Select.field(WarehouseMovement::binCode), "binCode").create();

			assertThat(movement.binCode()).contains("binCode");
		}

		@Test
		void lotCode_WhenLotCodeIsNull_ThenReturnEmptyOptional() {
			WarehouseMovement movement = Instancio.of(WarehouseMovement.class)
					.set(Select.field(WarehouseMovement::lotCode), null).create();

			assertThat(movement.lotCode()).isEmpty();
		}

		@Test
		void lotCode_WhenLotCodeIsNotNull_ThenReturnOptionalWithValue() {
			WarehouseMovement movement = Instancio.of(WarehouseMovement.class)
					.set(Select.field(WarehouseMovement::lotCode), "lotCode").create();

			assertThat(movement.lotCode()).contains("lotCode");
		}

		@Test
		void reference_WhenReferenceIsNull_ThenReturnEmptyOptional() {
			WarehouseMovement movement = Instancio.of(WarehouseMovement.class)
					.set(Select.field(WarehouseMovement::reference), null).create();

			assertThat(movement.reference()).isEmpty();
		}

		@Test
		void reference_WhenReferenceIsNotNull_ThenReturnOptionalWithValue() {
			MovementReference reference = new MovementReference("referenceId", ReferenceType.PURCHASE_ORDER);
			WarehouseMovement movement = Instancio.of(WarehouseMovement.class)
					.set(Select.field(WarehouseMovement::reference), reference).create();

			assertThat(movement.reference()).contains(reference);
		}
	}

	@Nested
	class BuilderValidation {

		@ParameterizedTest
		@NullAndEmptySource
		@ValueSource(strings = { "   " })
		void build_WhenMandatoryMovementIdMissing_ThenThrowValidationException(String movementId) {
			WarehouseMovementBuilder builder = validMovementBuilder().movementId(movementId);

			ValidationException exception = Assertions.catchThrowableOfType(ValidationException.class, builder::build);

			assertThat(exception.getMessage()).contains("movementId mandatory");
			assertThat(exception.getErrorCode()).isEqualTo(WarehouseMovementErrorCode.MISSING_REQUIRED_FIELD);
		}

		@ParameterizedTest
		@NullAndEmptySource
		@ValueSource(strings = { "   " })
		void build_WhenMandatoryProductSkuMissing_ThenThrowValidationException(String productSku) {
			WarehouseMovementBuilder builder = validMovementBuilder().productSku(productSku);

			ValidationException exception = Assertions.catchThrowableOfType(ValidationException.class, builder::build);

			assertThat(exception.getMessage()).contains("product mandatory");
			assertThat(exception.getErrorCode()).isEqualTo(WarehouseMovementErrorCode.MISSING_REQUIRED_FIELD);
		}

		@Test
		void build_WhenMandatoryTypeMissing_ThenThrowValidationException() {
			WarehouseMovementBuilder builder = validMovementBuilder().type(null);

			ValidationException exception = Assertions.catchThrowableOfType(ValidationException.class, builder::build);

			assertThat(exception.getMessage()).contains("type mandatory");
			assertThat(exception.getErrorCode()).isEqualTo(WarehouseMovementErrorCode.MISSING_REQUIRED_FIELD);
		}

		@ParameterizedTest
		@NullAndEmptySource
		@ValueSource(strings = { "   " })
		void build_WhenMandatoryOperatorCodeMissing_ThenThrowValidationException(String operatorCode) {
			WarehouseMovementBuilder builder = validMovementBuilder().operatorCode(operatorCode);

			ValidationException exception = Assertions.catchThrowableOfType(ValidationException.class, builder::build);

			assertThat(exception.getMessage()).contains("operatorCode mandatory");
			assertThat(exception.getErrorCode()).isEqualTo(WarehouseMovementErrorCode.MISSING_REQUIRED_FIELD);
		}

		@Test
		void build_WhenMandatoryMovementCreatedAtMissing_ThenThrowValidationException() {
			WarehouseMovementBuilder builder = validMovementBuilder().movementCreatedAt(null);

			ValidationException exception = Assertions.catchThrowableOfType(ValidationException.class, builder::build);

			assertThat(exception.getMessage()).contains("movementCreatedAt mandatory");
			assertThat(exception.getErrorCode()).isEqualTo(WarehouseMovementErrorCode.MISSING_REQUIRED_FIELD);
		}

		@Test
		void build_WhenMandatoryReceivedAtMissing_ThenThrowValidationException() {
			WarehouseMovementBuilder builder = validMovementBuilder().receivedAt(null);

			ValidationException exception = Assertions.catchThrowableOfType(ValidationException.class, builder::build);

			assertThat(exception.getMessage()).contains("receivedAt mandatory");
			assertThat(exception.getErrorCode()).isEqualTo(WarehouseMovementErrorCode.MISSING_REQUIRED_FIELD);
		}

		@Test
		void build_WhenMandatoryStatusMissing_ThenThrowValidationException() {
			WarehouseMovementBuilder builder = validMovementBuilder().status(null);

			ValidationException exception = Assertions.catchThrowableOfType(ValidationException.class, builder::build);

			assertThat(exception.getMessage()).contains("status mandatory");
			assertThat(exception.getErrorCode()).isEqualTo(WarehouseMovementErrorCode.MISSING_REQUIRED_FIELD);
		}

		@ParameterizedTest
		@ValueSource(ints = { 0, -1 })
		void validateWhCode_WhenMandatoryWhCodeMissing_ThenThrowValidationException(int whCode) {
			WarehouseMovementBuilder builder = validMovementBuilder().whCode(whCode);

			ValidationException exception = Assertions.catchThrowableOfType(ValidationException.class, builder::build);

			assertThat(exception.getMessage()).contains("whCode must be greater than 0");
			assertThat(exception.getErrorCode()).isEqualTo(WarehouseMovementErrorCode.INVALID_VALUE);
		}

		@ParameterizedTest
		@ValueSource(ints = { 0, -1 })
		void validateQty_WhenMandatoryQtyMissing_ThenThrowValidationException(int qty) {
			WarehouseMovementBuilder builder = validMovementBuilder().qty(qty);

			ValidationException exception = Assertions.catchThrowableOfType(ValidationException.class, builder::build);

			assertThat(exception.getMessage()).contains("qty must be greater than 0");
			assertThat(exception.getErrorCode()).isEqualTo(WarehouseMovementErrorCode.INVALID_VALUE);
		}

		@Test
		void validateReference_WhenReferenceIsPresentWhenShouldBeForbidden_ThenThrowValidationException() {
			WarehouseMovementBuilder builder = validMovementBuilder().type(MovementType.ADJUSTMENT)
					.reference(new MovementReference(null, null));

			ValidationException exception = Assertions.catchThrowableOfType(ValidationException.class, builder::build);

			assertThat(exception.getMessage())
					.contains(WarehouseMovementErrorCode.ADJUSTMENT_REFERENCE_NOT_ALLOWED.getDescr());
			assertThat(exception.getErrorCode()).isEqualTo(WarehouseMovementErrorCode.ADJUSTMENT_REFERENCE_NOT_ALLOWED);

		}

		@ParameterizedTest
		@EnumSource(value = MovementType.class, names = { "ADJUSTMENT" }, mode = EnumSource.Mode.EXCLUDE)
		void validateReference_WhenMandatoryReferenceMissing_ThenThrowValidationException(MovementType type) {
			WarehouseMovementBuilder builder = validMovementBuilder().type(type).reference(null);

			ValidationException exception = Assertions.catchThrowableOfType(ValidationException.class, builder::build);

			assertThat(exception.getMessage()).contains("Movement type " + type + " requires reference");
			assertThat(exception.getErrorCode()).isEqualTo(WarehouseMovementErrorCode.MISSING_REFERENCE);

		}

		@Test
		void build_WhenAllMandatoryFieldsValidAndReferenceNotRequired_ThenBuildSuccessfully() {
			WarehouseMovementBuilder builder = validMovementBuilder().type(MovementType.ADJUSTMENT).reference(null);

			WarehouseMovement movement = builder.build();

			assertThat(movement.getMovementId()).isEqualTo("movementId");
			assertThat(movement.reference()).isEmpty();
		}

		@Test
		void build_WhenAllMandatoryFieldsValidAndReferenceRequired_ThenBuildSuccessfully() {
			MovementReference reference = new MovementReference("refId", ReferenceType.PURCHASE_ORDER);
			WarehouseMovementBuilder builder = validMovementBuilder().type(MovementType.INBOUND).reference(reference);

			WarehouseMovement movement = builder.build();

			assertThat(movement.reference()).contains(reference);
		}

		private static WarehouseMovementBuilder validMovementBuilder() {
			return WarehouseMovement.builder().movementId("movementId").productSku("productSku")
					.type(MovementType.INBOUND).operatorCode("operatorCode").movementCreatedAt(Instant.now())
					.receivedAt(Instant.now()).status(MovementStatus.PROCESSING).whCode(1).qty(1);
		}
	}

}
