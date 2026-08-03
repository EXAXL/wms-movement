package com.github.exaxl.wms.adapter.out.db.entity;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class MovementNaturalIdTest {

	@Test
	void equals_WhenMovementIdAndWhcodeAreEquals_ThenTwoObjectAreEqual() {
		MovementNaturalId mid1 = new MovementNaturalId("M123", 1);
		MovementNaturalId mid2 = new MovementNaturalId("M123", 1);
		
		assertThat(mid1).isEqualTo(mid2);
	}
	
	@Test
	void equals_WhenMovementIdAndWhcodeAreNotEquals_ThenTwoObjectAreNotEqual() {
		MovementNaturalId mid1 = new MovementNaturalId("M123", 1);
		MovementNaturalId mid2 = new MovementNaturalId("M123", 2);
		MovementNaturalId mid3 = new MovementNaturalId("M124", 1);
		
		assertThat(mid1).isNotEqualTo(mid2);
		assertThat(mid1).isNotEqualTo(mid3);
	}
	
	@Test
	void hashCode_WhenMovementIdAndWhcodeAreEquals_ThenTwoObjectHaveSameHashCode() {
		MovementNaturalId mid1 = new MovementNaturalId("M123", 1);
		MovementNaturalId mid2 = new MovementNaturalId("M123", 1);
		
		assertThat(mid1).hasSameHashCodeAs(mid2);
	}
	
	@Test
	void hashCode_WhenMovementIdAndWhcodeAreNotEquals_ThenTwoObjectHaveDifferentHashCode() {
		MovementNaturalId mid1 = new MovementNaturalId("M123", 1);
		MovementNaturalId mid2 = new MovementNaturalId("M123", 2);
		MovementNaturalId mid3 = new MovementNaturalId("M124", 1);
		
		assertThat(mid1).doesNotHaveSameHashCodeAs(mid2);
		assertThat(mid1).doesNotHaveSameHashCodeAs(mid3);
	}
}
