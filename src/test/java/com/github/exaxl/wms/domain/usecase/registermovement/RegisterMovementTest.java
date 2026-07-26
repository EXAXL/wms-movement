package com.github.exaxl.wms.domain.usecase.registermovement;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Optional;
import java.util.Set;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.github.exaxl.wms.adapter.in.messaging.dto.MovementMessageDto;
import com.github.exaxl.wms.adapter.out.db.repository.MockedBinRepository.BinValidationProjection;
import com.github.exaxl.wms.adapter.out.db.repository.MockedProductRepository.ProductValidationProjection;
import com.github.exaxl.wms.adapter.out.db.repository.MockedWarehouseRepository.WarehouseValidationProjection;
import com.github.exaxl.wms.domain.exception.BinNotFoundException;
import com.github.exaxl.wms.domain.exception.BinZoneIncompatibleException;
import com.github.exaxl.wms.domain.exception.DuplicateMovementException;
import com.github.exaxl.wms.domain.exception.ProductNotAllowedException;
import com.github.exaxl.wms.domain.exception.ProductNotFoundException;
import com.github.exaxl.wms.domain.exception.WarehouseNotActiveException;
import com.github.exaxl.wms.domain.exception.WarehouseNotFoundException;
import com.github.exaxl.wms.domain.model.movement.MovementStatus;
import com.github.exaxl.wms.domain.model.movement.MovementType;
import com.github.exaxl.wms.domain.model.movement.WarehouseMovement;
import com.github.exaxl.wms.domain.model.product.ProductAttribute;
import com.github.exaxl.wms.domain.model.product.ProductStatus;
import com.github.exaxl.wms.domain.model.warehouse.BinStatus;
import com.github.exaxl.wms.domain.model.warehouse.WarehouseStatus;
import com.github.exaxl.wms.domain.model.warehouse.ZoneAttribute;
import com.github.exaxl.wms.domain.service.BinService;
import com.github.exaxl.wms.domain.service.BinService.BinValidationResult;
import com.github.exaxl.wms.domain.service.MovementService;
import com.github.exaxl.wms.domain.service.ProductService;
import com.github.exaxl.wms.domain.service.ProductService.ProductValidationResult;
import com.github.exaxl.wms.domain.service.WarehouseService;

class RegisterMovementTest {

	private static final String MOVEMENT_ID = "MOV-001";
	private static final int WAREHOUSE_CODE = 1;
	private static final String PRODUCT_SKU = "SKU-001";
	private static final String OPERATOR_CODE = "OP-001";

	private final MovementService movementService = Mockito.mock(MovementService.class);
	private final WarehouseService warehouseService = Mockito.mock(WarehouseService.class);
	private final ProductService productService = Mockito.mock(ProductService.class);
	private final BinService binService = Mockito.mock(BinService.class);
	private final MovementPersistenceManager movementPersistenceManager = Mockito
			.mock(MovementPersistenceManager.class);

	private final RegisterMovement registerMovement = new RegisterMovement(movementService, warehouseService,
			productService, binService, movementPersistenceManager);

	@Nested
	class ErrorPathTests {
		@Test
		void register_WhenMovementIsDuplicate_ThenThrowsExceptionAndStopsProcessing() {
			MovementMessageDto dto = validDto(null);

			Mockito.doThrow(new DuplicateMovementException(
					"Duplicate movement detected - movementId: " + MOVEMENT_ID + ", warehouseCode: " + WAREHOUSE_CODE))
					.when(movementService).ensureNotDuplicate(MOVEMENT_ID, WAREHOUSE_CODE);

			Assertions.assertThatThrownBy(() -> registerMovement.register(dto))
					.isInstanceOf(DuplicateMovementException.class).hasMessageContaining("Duplicate movement detected");

			Mockito.verify(warehouseService, Mockito.never()).requireActiveWarehouse(Mockito.any(Integer.class));
			Mockito.verifyNoInteractions(productService, binService, movementPersistenceManager);
		}

		@Test
		void register_WhenWarehouseNotFound_ThenThrowsExceptionAndStopsProcessing() {
			MovementMessageDto dto = validDto(null);

			stubDuplicateCheckPasses();

			Mockito.when(warehouseService.requireActiveWarehouse(WAREHOUSE_CODE))
					.thenThrow(new WarehouseNotFoundException("Warehouse " + WAREHOUSE_CODE + " not found"));

			Assertions.assertThatThrownBy(() -> registerMovement.register(dto))
					.isInstanceOf(WarehouseNotFoundException.class)
					.hasMessageContaining("Warehouse " + WAREHOUSE_CODE + " not found");

			Mockito.verifyNoInteractions(productService, binService, movementPersistenceManager);
		}

		@Test
		void register_WhenWarehouseNotActive_ThenThrowsExceptionAndStopsProcessing() {
			MovementMessageDto dto = validDto(null);

			stubDuplicateCheckPasses();

			Mockito.when(warehouseService.requireActiveWarehouse(WAREHOUSE_CODE))
					.thenThrow(new WarehouseNotActiveException("Warehouse " + WAREHOUSE_CODE + " is not active"));

			Assertions.assertThatThrownBy(() -> registerMovement.register(dto))
					.isInstanceOf(WarehouseNotActiveException.class)
					.hasMessageContaining("Warehouse " + WAREHOUSE_CODE + " is not active");

			Mockito.verifyNoInteractions(productService, binService, movementPersistenceManager);
		}

		@Test
		void register_WhenProductNotFound_ThenThrowsExceptionAndStopsProcessing() {
			MovementMessageDto dto = validDto(null);

			stubDuplicateCheckPasses();
			stubActiveWarehouse();

			Mockito.when(productService.requireAllowedProduct(PRODUCT_SKU, MovementType.ADJUSTMENT))
					.thenThrow(new ProductNotFoundException("Product with SKU " + PRODUCT_SKU + " not found"));

			Assertions.assertThatThrownBy(() -> registerMovement.register(dto))
					.isInstanceOf(ProductNotFoundException.class)
					.hasMessageContaining("Product with SKU " + PRODUCT_SKU + " not found");

			Mockito.verifyNoInteractions(binService, movementPersistenceManager);
		}

		@Test
		void register_WhenProductNotAllowedForMovementType_ThenThrowsExceptionAndStopsProcessing() {
			MovementMessageDto dto = validDto(null);

			stubDuplicateCheckPasses();
			stubActiveWarehouse();

			Mockito.when(productService.requireAllowedProduct(PRODUCT_SKU, MovementType.ADJUSTMENT))
					.thenThrow(new ProductNotAllowedException("Product with SKU " + PRODUCT_SKU
							+ " is not allowed for movement type " + MovementType.ADJUSTMENT));

			Assertions.assertThatThrownBy(() -> registerMovement.register(dto))
					.isInstanceOf(ProductNotAllowedException.class)
					.hasMessageContaining("is not allowed for movement type");

			Mockito.verifyNoInteractions(binService, movementPersistenceManager);
		}

		@Test
		void register_WhenBinNotFound_ThenThrowsExceptionAndStopsProcessing() {
			String binCode = "BIN-404";
			MovementMessageDto dto = validDto(binCode);

			stubDuplicateCheckPasses();
			stubActiveWarehouse();
			stubAllowedProduct(Set.of(ProductAttribute.of("FRAGILE")));

			Mockito.when(binService.validateBin(binCode, WAREHOUSE_CODE, Set.of(ProductAttribute.of("FRAGILE"))))
					.thenThrow(new BinNotFoundException("Bin not found: " + binCode));

			Assertions.assertThatThrownBy(() -> registerMovement.register(dto)).isInstanceOf(BinNotFoundException.class)
					.hasMessageContaining("Bin not found: " + binCode);

			Mockito.verifyNoInteractions(movementPersistenceManager);
		}

		@Test
		void register_WhenBinZoneIncompatibleWithProductAttributes_ThenThrowsExceptionAndStopsProcessing() {
			String binCode = "BIN-HAZ";
			MovementMessageDto dto = validDto(binCode);

			stubDuplicateCheckPasses();
			stubActiveWarehouse();
			stubAllowedProduct(Set.of(ProductAttribute.of("FROZEN")));

			Mockito.when(binService.validateBin(binCode, WAREHOUSE_CODE, Set.of(ProductAttribute.of("FROZEN"))))
					.thenThrow(new BinZoneIncompatibleException(
							"Bin zone attributes are not compatible with product attributes for bin: " + binCode));

			Assertions.assertThatThrownBy(() -> registerMovement.register(dto))
					.isInstanceOf(BinZoneIncompatibleException.class)
					.hasMessageContaining("not compatible with product attributes for bin: " + binCode);

			Mockito.verifyNoInteractions(movementPersistenceManager);
		}

	}

	@Nested
	class HappyPathTests{
		
		@Test
		void register_WhenNoBinCodeProvided_ThenRegistersMovementWithoutBinAndNoWarning() {
			MovementMessageDto dto = validDto(null);
			
			stubDuplicateCheckPasses();
			stubActiveWarehouse();
			stubAllowedProduct(Set.of(ProductAttribute.of("FRAGILE")));
			Mockito.when(binService.validateBin(null, WAREHOUSE_CODE, Set.of(ProductAttribute.of("FRAGILE"))))
					.thenReturn(Optional.empty());
	 
			registerMovement.register(dto);
	 

			ArgumentCaptor<WarehouseMovement> captor = ArgumentCaptor.forClass(WarehouseMovement.class);
			Mockito.verify(movementPersistenceManager, Mockito.times(1)).registerMovementAndOutboxEvent(captor.capture());
	 
			WarehouseMovement registered = captor.getValue();
			assertThat(registered.getMovementId()).isEqualTo(MOVEMENT_ID);
			assertThat(registered.getWhCode()).isEqualTo(WAREHOUSE_CODE);
			assertThat(registered.getProductSku()).isEqualTo(PRODUCT_SKU);
			assertThat(registered.getStatus()).isEqualTo(MovementStatus.RECEIVED);
			assertThat(registered.binCode()).as("bin code must be empty when no bin was validated").isEmpty();
		}

		@Test
		void register_WhenBinIsAvailable_ThenRegistersMovementWithBinCodeAndNoWarning() {
			String binCode = "BIN-001";
			MovementMessageDto dto = validDto(binCode);
			
			stubDuplicateCheckPasses();
			stubActiveWarehouse();
			stubAllowedProduct(Set.of(ProductAttribute.of("FRAGILE")));
	 
			BinValidationProjection binProjection = new BinValidationProjection(binCode, WAREHOUSE_CODE,
					BinStatus.of("AVAILABLE"), Set.of(ZoneAttribute.of("BULK")));
			BinValidationResult binResult = new BinValidationResult(binProjection, false, Optional.empty());
			Mockito.when(binService.validateBin(binCode, WAREHOUSE_CODE, Set.of(ProductAttribute.of("FRAGILE"))))
					.thenReturn(Optional.of(binResult));
	 
			registerMovement.register(dto);
	 
			ArgumentCaptor<WarehouseMovement> captor = ArgumentCaptor.forClass(WarehouseMovement.class);
			Mockito.verify(movementPersistenceManager, Mockito.times(1)).registerMovementAndOutboxEvent(captor.capture());
	 
			WarehouseMovement registered = captor.getValue();
			assertThat(registered.getStatus()).isEqualTo(MovementStatus.RECEIVED);
			assertThat(registered.binCode()).contains(binCode);
		}

		@Test
		void register_WhenBinHasWarning_ThenRegistersMovementWithReceivedWithWarningsStatus() {
			String binCode = "BIN-002";
			MovementMessageDto dto = validDto(binCode);
			
			stubDuplicateCheckPasses();
			stubActiveWarehouse();
			stubAllowedProduct(Set.of(ProductAttribute.of("REFRIGERATED")));
	 
			BinValidationProjection binProjection = new BinValidationProjection(binCode, WAREHOUSE_CODE,
					BinStatus.of("OCCUPIED"), Set.of(ZoneAttribute.of("REFRIGERATED")));
			BinValidationResult binResult = new BinValidationResult(binProjection, true,
					Optional.of("Bin status is not AVAILABLE"));
			Mockito.when(binService.validateBin(binCode, WAREHOUSE_CODE, Set.of(ProductAttribute.of("REFRIGERATED"))))
					.thenReturn(Optional.of(binResult));
	 
			registerMovement.register(dto);
	 
			ArgumentCaptor<WarehouseMovement> captor = ArgumentCaptor.forClass(WarehouseMovement.class);
			Mockito.verify(movementPersistenceManager, Mockito.times(1)).registerMovementAndOutboxEvent(captor.capture());
	 
			WarehouseMovement registered = captor.getValue();
			assertThat(registered.getStatus()).as("a bin warning must downgrade the movement status")
					.isEqualTo(MovementStatus.RECEIVED_WITH_WARNINGS);
			assertThat(registered.binCode()).contains(binCode);
		}

	}
	
	private static MovementMessageDto validDto(String binCode) {
		return new MovementMessageDto(MOVEMENT_ID, WAREHOUSE_CODE, MovementType.ADJUSTMENT, OPERATOR_CODE,
				Instant.now(), null, null, PRODUCT_SKU, 5, binCode, "LOT-001");
	}

	private void stubDuplicateCheckPasses() {
		Mockito.doNothing().when(movementService).ensureNotDuplicate(MOVEMENT_ID, WAREHOUSE_CODE);
	}

	private void stubActiveWarehouse() {
		Mockito.when(warehouseService.requireActiveWarehouse(WAREHOUSE_CODE)).thenReturn(
				new WarehouseValidationProjection(WAREHOUSE_CODE, WarehouseStatus.of("ACTIVE"), ZoneId.of("UTC")));
	}

	private void stubAllowedProduct(Set<ProductAttribute> attributes) {
		Mockito.when(productService.requireAllowedProduct(PRODUCT_SKU, MovementType.ADJUSTMENT))
				.thenReturn(new ProductValidationResult(
						new ProductValidationProjection(PRODUCT_SKU, ProductStatus.of("ACTIVE"), attributes)));
	}

}
