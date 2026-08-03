package com.github.exaxl.wms.adapter.out.db.entity;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class MovementNaturalId {
	private String movementId;
	private int whCode;
}
