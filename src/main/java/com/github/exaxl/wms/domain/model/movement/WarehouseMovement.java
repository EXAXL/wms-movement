package com.github.exaxl.wms.domain.model.movement;

import java.time.Instant;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.github.exaxl.error.exception.core.ValidationException;
import com.github.exaxl.wms.domain.enums.WarehouseMovementErrorCode;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(buildMethodName = "internalBuild")
public class WarehouseMovement {

	@EqualsAndHashCode.Include
	private String movementId;
	@EqualsAndHashCode.Include
	private int whCode;
	private String productSku;

	private MovementType type;
	private int qty;
	private String operatorCode;

	private String referenceId;
	private ReferenceType referenceType;

	private Instant movementCreatedAt;
	private Instant receivedAt;

	private MovementStatus status;

	@Getter(AccessLevel.NONE)
	private String binCode;

	@Getter(AccessLevel.NONE)
	private String lotCode;

	@Getter(AccessLevel.NONE)
	private MovementReference reference;

	public Optional<String> binCode() {
		return Optional.ofNullable(binCode);
	}

	public Optional<String> lotCode() {
		return Optional.ofNullable(lotCode);
	}

	public Optional<MovementReference> reference() {
		return Optional.ofNullable(reference);
	}

	public static record MovementReference(String referenceId, ReferenceType referenceType) {
	}

	public static class WarehouseMovementBuilder {

		public WarehouseMovement build() {
			// constraints validation
			validateMandatoryFields();
			validateWhCode();
			validateQty();
			validateReference();

			return this.internalBuild();
		}

		protected void validateMandatoryFields() {
			if (StringUtils.isBlank(movementId)) {
				throw new ValidationException("movementId mandatory",
						WarehouseMovementErrorCode.MISSING_REQUIRED_FIELD);
			}

			if (StringUtils.isBlank(productSku)) {
				throw new ValidationException("product mandatory", WarehouseMovementErrorCode.MISSING_REQUIRED_FIELD);
			}

			if (type == null) {
				throw new ValidationException("type mandatory", WarehouseMovementErrorCode.MISSING_REQUIRED_FIELD);
			}

			if (StringUtils.isBlank(operatorCode)) {
				throw new ValidationException("operatorCode mandatory",
						WarehouseMovementErrorCode.MISSING_REQUIRED_FIELD);
			}

			if (movementCreatedAt == null) {
				throw new ValidationException("movementCreatedAt mandatory",
						WarehouseMovementErrorCode.MISSING_REQUIRED_FIELD);
			}

			if (receivedAt == null) {
				throw new ValidationException("receivedAt mandatory",
						WarehouseMovementErrorCode.MISSING_REQUIRED_FIELD);
			}

			if (status == null) {
				throw new ValidationException("status mandatory", WarehouseMovementErrorCode.MISSING_REQUIRED_FIELD);
			}
		}

		protected void validateWhCode() {
			if (whCode <= 0) {
				throw new ValidationException("whCode must be greater than 0",
						WarehouseMovementErrorCode.INVALID_VALUE);
			}
		}

		protected void validateQty() {
			if (qty <= 0) {
				throw new ValidationException("qty must be greater than 0", WarehouseMovementErrorCode.INVALID_VALUE);
			}
		}

		protected void validateReference() {
			if (MovementRules.forbidsReference(type) && reference != null) {
				throw new ValidationException(WarehouseMovementErrorCode.ADJUSTMENT_REFERENCE_NOT_ALLOWED);
			}

			if (MovementRules.requiresReference(type) && reference == null) {
				throw new ValidationException("Movement type " + type + " requires reference",
						WarehouseMovementErrorCode.MISSING_REFERENCE);
			}
		}
	}
}
