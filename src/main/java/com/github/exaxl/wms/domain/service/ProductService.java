package com.github.exaxl.wms.domain.service;

import org.springframework.stereotype.Service;

import com.github.exaxl.wms.adapter.out.db.repository.MockedProductRepository;
import com.github.exaxl.wms.adapter.out.db.repository.MockedProductRepository.ProductValidationProjection;
import com.github.exaxl.wms.domain.exception.ProductNotAllowedException;
import com.github.exaxl.wms.domain.exception.ProductNotFoundException;
import com.github.exaxl.wms.domain.model.movement.MovementType;
import com.github.exaxl.wms.domain.model.movement.ProductMovementRules;
import com.github.exaxl.wms.domain.model.product.ProductStatus;

@Service
public class ProductService {

	private final MockedProductRepository mockedProductRepository;

	public ProductService(MockedProductRepository mockedProductRepository) {
		this.mockedProductRepository = mockedProductRepository;
	}

	public ProductValidationResult requireAllowedProduct(String sku, MovementType movementType) {
		ProductValidationProjection product = mockedProductRepository.findBySku(sku)
				.orElseThrow(() -> new ProductNotFoundException("Product with SKU " + sku + " not found"));

		if (!isProductAllowedForMovement(product, movementType)) {
			throw new ProductNotAllowedException(
					"Product with SKU " + sku + " is not allowed for movement type " + movementType);
		}

		return new ProductValidationResult(product);
	}

	protected boolean isProductAllowedForMovement(ProductValidationProjection product, MovementType movementType) {
		boolean isAllowed = true;

		if (ProductStatus.DISCONTINUED.equals(product.status())
				&& !ProductMovementRules.isProductAllowedForDiscontinued(movementType)) {
			isAllowed = false;
		}

		return isAllowed;
	}
	
	public record ProductValidationResult(ProductValidationProjection product) {}
}
