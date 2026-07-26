package com.github.exaxl.wms.domain.model.movement;

import java.util.Set;

public final class ProductMovementRules {

    private static final Set<MovementType> ALLOWED_FOR_DISCONTINUED = Set.of(
            MovementType.TRANSFER_IN,
            MovementType.TRANSFER_OUT,
            MovementType.ADJUSTMENT,
            MovementType.RETURN
        );
	
    public static boolean isProductAllowedForDiscontinued(MovementType movementType) {
		return ALLOWED_FOR_DISCONTINUED.contains(movementType);
	}
    
	private ProductMovementRules() {}
}
