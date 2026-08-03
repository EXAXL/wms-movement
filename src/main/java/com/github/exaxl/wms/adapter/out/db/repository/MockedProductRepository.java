package com.github.exaxl.wms.adapter.out.db.repository;

import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.github.exaxl.wms.domain.model.product.ProductAttribute;
import com.github.exaxl.wms.domain.model.product.ProductStatus;

@Component
public class MockedProductRepository {

	public Optional<ProductValidationProjection> findBySku(String sku) {
		// TODO QUERY PER VEDERE SE IL PRODOTTO ESISTE, SE NON ESISTE RITORNA
		// Optional.empty()
		return Optional.of(new ProductValidationProjection(sku, ProductStatus.of("ACTIVE"),
				Set.of(ProductAttribute.of("HAZARDOUS"), ProductAttribute.of("FRAGILE"))));
	}

	public record ProductValidationProjection(String sku, ProductStatus status, Set<ProductAttribute> attributes) {
	}
}
