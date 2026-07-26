package com.github.exaxl.wms.domain.model.product;

import java.util.Map;
import java.util.Set;

import com.github.exaxl.wms.domain.model.warehouse.ZoneAttribute;

public final class ProductZoneCompatibilityRules {

	private static final String HIGH_VALUE = "HIGH_VALUE";
	private static final String FROZEN = "FROZEN";
	private static final String REFRIGERATED = "REFRIGERATED";
	private static final String HAZARDOUS = "HAZARDOUS";
	
	
	private static final Map<ProductAttribute, Set<ZoneAttribute>> COMPATIBILITY_RULES_PRODUCT_ZONE_ATTRIBUTE = Map.of(
			ProductAttribute.of(HAZARDOUS), Set.of(ZoneAttribute.of(HAZARDOUS)), ProductAttribute.of(REFRIGERATED),
			Set.of(ZoneAttribute.of(REFRIGERATED)), ProductAttribute.of(FROZEN), Set.of(ZoneAttribute.of(FROZEN)),
			ProductAttribute.of(HIGH_VALUE), Set.of(ZoneAttribute.of(HIGH_VALUE))
	// FRAGILE → No specific zone attribute required, so not included in the map
	);

	public static boolean isCompatible(Set<ProductAttribute> productAttrs, Set<ZoneAttribute> zoneAttrs) {
		return productAttrs.stream().filter(COMPATIBILITY_RULES_PRODUCT_ZONE_ATTRIBUTE::containsKey).allMatch(
				attr -> zoneAttrs.stream().anyMatch(COMPATIBILITY_RULES_PRODUCT_ZONE_ATTRIBUTE.get(attr)::contains));
	}

	private ProductZoneCompatibilityRules() {
	}
}
