package com.github.exaxl.wms.domain.model.movement;

public final class MovementRules {

	public static boolean requiresReference(MovementType type) {
		return !MovementType.ADJUSTMENT.equals(type);
	}

	/*
	 * Forbids is not the opposite of requires. Forbids means that the reference is
	 * not allowed, while requires means that the reference is mandatory. For
	 * example, a movement type that forbids reference can still be created without
	 * a reference, but a movement type that requires reference cannot be created
	 * without a reference.
	 */
	public static boolean forbidsReference(MovementType type) {
		return MovementType.ADJUSTMENT.equals(type);
	}

	private MovementRules() {
	}
}
