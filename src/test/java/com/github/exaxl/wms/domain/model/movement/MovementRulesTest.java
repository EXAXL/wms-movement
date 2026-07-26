package com.github.exaxl.wms.domain.model.movement;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class MovementRulesTest {

	@ParameterizedTest(name = "MovementType({0})")
	@EnumSource(value =MovementType.class, names = { "ADJUSTMENT" }, mode = EnumSource.Mode.EXCLUDE)
	void requiresReference_WhenMovementTypeIsNotAdjust_ThenReturnTrue(MovementType type) {
		assertThat(MovementRules.requiresReference(type)).isTrue();
	}

	@Test
	void requiresReference_WhenMovementTypeIsAdjust_ThenReturnFalse() {
		assertThat(MovementRules.requiresReference(MovementType.ADJUSTMENT)).isFalse();
	}
	
	@ParameterizedTest(name = "MovementType({0})")
	@EnumSource(value =MovementType.class, names = { "ADJUSTMENT" }, mode = EnumSource.Mode.EXCLUDE)
	void forbidsReference_WhenMovementTypeIsNotAdjust_ThenReturnFalse(MovementType type) {
		assertThat(MovementRules.forbidsReference(type)).isFalse();
	}
	
	@Test
	void forbidsReference_WhenMovementTypeIsAdjust_ThenReturnTrue() {
		assertThat(MovementRules.forbidsReference(MovementType.ADJUSTMENT)).isTrue();
	}
}
