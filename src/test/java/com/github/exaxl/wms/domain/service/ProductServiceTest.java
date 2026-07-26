package com.github.exaxl.wms.domain.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import com.github.exaxl.wms.adapter.out.db.repository.MockedProductRepository;
import com.github.exaxl.wms.adapter.out.db.repository.MockedProductRepository.ProductValidationProjection;
import com.github.exaxl.wms.domain.exception.ProductNotAllowedException;
import com.github.exaxl.wms.domain.exception.ProductNotFoundException;
import com.github.exaxl.wms.domain.model.movement.MovementType;
import com.github.exaxl.wms.domain.model.product.ProductAttribute;
import com.github.exaxl.wms.domain.model.product.ProductStatus;
import com.github.exaxl.wms.domain.service.ProductService.ProductValidationResult;

class ProductServiceTest {

	private MockedProductRepository mockedProductRepository = Mockito.mock(MockedProductRepository.class);
	private ProductService service = new ProductService(mockedProductRepository);

	@Test
	void isProductAllowedForMovement_WhenProductIsNotDiscountinued_ThenReturnTrue() {
		ProductValidationProjection productValidationProj = new ProductValidationProjection("SKU123",
				ProductStatus.of("ACTIVE"), Set.of(ProductAttribute.of("HAZARDOUS")));
		MovementType movementType = MovementType.INBOUND;

		boolean result = service.isProductAllowedForMovement(productValidationProj, movementType);

		assertThat(result).isTrue();
	}

	@Test
	void isProductAllowedForMovement_WhenProductIsDiscountinuedAndMovementTypeIsNotAllowed_ThenReturnFalse() {
		ProductValidationProjection productValidationProj = new ProductValidationProjection("SKU123",
				ProductStatus.of("DISCONTINUED"), Set.of(ProductAttribute.of("HAZARDOUS")));
		MovementType movementType = MovementType.INBOUND;

		boolean result = service.isProductAllowedForMovement(productValidationProj, movementType);

		assertThat(result).isFalse();
	}

	@Test
	void isProductAllowedForMovement_WhenProductIsDiscountinuedAndMovementTypeIsAllowed_ThenReturnTrue() {
		ProductValidationProjection productValidationProj = new ProductValidationProjection("SKU123",
				ProductStatus.of("DISCONTINUED"), Set.of(ProductAttribute.of("HAZARDOUS")));
		MovementType movementType = MovementType.TRANSFER_IN;

		boolean result = service.isProductAllowedForMovement(productValidationProj, movementType);

		assertThat(result).isTrue();
	}

	@Test
	void requireAllowedProduct_WhenProductIsNotFound_ThenThrowProductNotFoundException() {
		String sku = "SKU123";
		MovementType movementType = MovementType.INBOUND;

		Mockito.when(mockedProductRepository.findBySku(sku)).thenReturn(Optional.empty());

		Assertions.assertThatThrownBy(() -> service.requireAllowedProduct(sku, movementType))
				.isInstanceOf(ProductNotFoundException.class)
				.hasMessageContaining("Product with SKU " + sku + " not found");
	}

	@Test
	void requireAllowedProduct_WhenProductIsNotAllowed_ThenThrowProductNotAllowedException() {
		String sku = "SKU123";
		MovementType movementType = MovementType.INBOUND;
		ProductValidationProjection productValidationProj = new ProductValidationProjection(sku,
				ProductStatus.of("DISCONTINUED"), Set.of(ProductAttribute.of("HAZARDOUS")));

		Mockito.when(mockedProductRepository.findBySku(sku)).thenReturn(Optional.of(productValidationProj));

		Assertions.assertThatThrownBy(() -> service.requireAllowedProduct(sku, movementType))
				.isInstanceOf(ProductNotAllowedException.class)
				.hasMessageContaining("Product with SKU " + sku + " is not allowed for movement type " + movementType);
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("allowedProductScenarios")
	void requireAllowedProduct_WhenProductIsAllowed_ThenReturnProductValidationResult(String scenario,
			ProductValidationProjection productValidationProj, MovementType movementType) {
		Mockito.when(mockedProductRepository.findBySku(productValidationProj.sku())).thenReturn(Optional.of(productValidationProj));

		ProductValidationResult result = service.requireAllowedProduct(productValidationProj.sku(), movementType);

		assertThat(result.product()).isEqualTo(productValidationProj);
	}

	private static Stream<Arguments> allowedProductScenarios() {
		return Stream.of(
				Arguments.of("active product, any movement",
						new ProductValidationProjection("SKU123", ProductStatus.of("ACTIVE"),
								Set.of(ProductAttribute.of("HAZARDOUS"))),
						MovementType.INBOUND),
				Arguments.of("discontinued product, movement allowed for discontinued",
						new ProductValidationProjection("SKU123", ProductStatus.of("DISCONTINUED"),
								Set.of(ProductAttribute.of("HAZARDOUS"))),
						MovementType.TRANSFER_IN));
	}
}
