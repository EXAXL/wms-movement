package com.github.exaxl.wms.domain.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import java.util.Set;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import com.github.exaxl.wms.adapter.out.db.repository.MockedBinRepository;
import com.github.exaxl.wms.adapter.out.db.repository.MockedBinRepository.BinValidationProjection;
import com.github.exaxl.wms.domain.exception.BinNotFoundException;
import com.github.exaxl.wms.domain.exception.BinZoneIncompatibleException;
import com.github.exaxl.wms.domain.model.product.ProductAttribute;
import com.github.exaxl.wms.domain.model.warehouse.BinStatus;
import com.github.exaxl.wms.domain.model.warehouse.ZoneAttribute;

@ExtendWith(OutputCaptureExtension.class)
class BinServiceTest {

	private MockedBinRepository mockedBinRepository = Mockito.mock(MockedBinRepository.class);
	private BinService binService = new BinService(mockedBinRepository);

	@ParameterizedTest(name = "binCode=[{0}] -> Optional.empty()")
	@NullAndEmptySource
	@ValueSource(strings = { "   " })
	void validateBin_WhenBinCodeIsNullOrEmpty_ThenReturnEmpty(String binCode) {
		assertThat(binService.validateBin(binCode, 1, null)).isEmpty();
	}

	@Test
	void validateBin_WhenBinNotFound_ThenThrowException() {
		String binCode = "BIN123";
		int warehouseCode = 1;

		Mockito.when(mockedBinRepository.findByCodeAndWarehouseCode(binCode, warehouseCode))
				.thenReturn(Optional.empty());

		Assertions.assertThatThrownBy(() -> binService.validateBin(binCode, warehouseCode, null))
				.isInstanceOf(BinNotFoundException.class).hasMessageContaining("Bin not found: BIN123");
	}

	@Test
	void validateBin_WhenProductAttributesAreIncompatibleWithBinZone_ThenThrowException() {
		String binCode = "BIN123";
		int warehouseCode = 1;
		BinValidationProjection binProjection = new BinValidationProjection(binCode, warehouseCode, BinStatus.of("AVAILABLE"),
				Set.of(ZoneAttribute.of("HAZARDOUS")));

		// Mock the repository to return a bin with incompatible zone attributes
		Mockito.when(mockedBinRepository.findByCodeAndWarehouseCode(binCode, warehouseCode))
				.thenReturn(Optional.of(binProjection));

		Set<ProductAttribute> productAttributes = Set.of(ProductAttribute.of("FROZEN"));
		Assertions
				.assertThatThrownBy(
						() -> binService.validateBin(binCode, warehouseCode, productAttributes))
				.isInstanceOf(BinZoneIncompatibleException.class)
				.hasMessageContaining("Bin zone attributes are not compatible with product attributes for bin: BIN123");
	}

	@Test
	void validateBin_WhenBinStatusIsNotAvailable_ThenReturnWarning() {
		String binCode = "BIN123";
		int warehouseCode = 1;
		BinValidationProjection binProjection = new BinValidationProjection(binCode, warehouseCode,
				BinStatus.of("OCCUPIED"), Set.of(ZoneAttribute.of("REFRIGERATED")));

		// Mock the repository to return a bin with status not available
		Mockito.when(mockedBinRepository.findByCodeAndWarehouseCode(binCode, warehouseCode))
				.thenReturn(Optional.of(binProjection));

		Set<ProductAttribute> productAttributes = Set.of(ProductAttribute.of("REFRIGERATED"));
		Optional<BinService.BinValidationResult> result = binService.validateBin(binCode, warehouseCode,
				productAttributes);

		assertThat(result).isPresent();
		assertThat(result.get().hasWarning()).isTrue();
		assertThat(result.get().warningReason()).contains("Bin status is not AVAILABLE");
		
	}
	
	@Test
	void validateBin_WhenBinNotAvailable_ThenLogsWarningForAlerting(CapturedOutput output) {
		String binCode = "BIN123";
		int warehouseCode = 1;
		BinValidationProjection binProjection = new BinValidationProjection(binCode, warehouseCode,
				BinStatus.of("OCCUPIED"), Set.of(ZoneAttribute.of("REFRIGERATED")));

		// Mock the repository to return a bin with status not available
		Mockito.when(mockedBinRepository.findByCodeAndWarehouseCode(binCode, warehouseCode))
				.thenReturn(Optional.of(binProjection));

		Set<ProductAttribute> productAttributes = Set.of(ProductAttribute.of("REFRIGERATED"));
		binService.validateBin(binCode, warehouseCode,
				productAttributes);
		
		assertThat(output).contains("Bin BIN123 is not available (status: OCCUPIED) — movement will be registered with warning");
	}
	
	
	@Test
	void validateBin_WhenBinStatusIsAvailable_ThenReturnNoWarning() {
		String binCode = "BIN123";
		int warehouseCode = 1;
		BinValidationProjection binProjection = new BinValidationProjection(binCode, warehouseCode,
				BinStatus.of("AVAILABLE"), Set.of(ZoneAttribute.of("REFRIGERATED")));

		// Mock the repository to return a bin with status available
		Mockito.when(mockedBinRepository.findByCodeAndWarehouseCode(binCode, warehouseCode))
				.thenReturn(Optional.of(binProjection));

		Set<ProductAttribute> productAttributes = Set.of(ProductAttribute.of("REFRIGERATED"));
		Optional<BinService.BinValidationResult> result = binService.validateBin(binCode, warehouseCode,
				productAttributes);

		assertThat(result).isPresent();
		assertThat(result.get().hasWarning()).isFalse();
		assertThat(result.get().warningReason()).isEmpty();
	}
	
	@Test
	void validateBin_WhenProductAttributesAreEmpty_ThenProceedWithOtherChecks() {
		String binCode = "BIN123";
		int warehouseCode = 1;
		BinValidationProjection binProjection = new BinValidationProjection(binCode, warehouseCode, BinStatus.of("AVAILABLE"),
				Set.of(ZoneAttribute.of("HAZARDOUS")));

		// Mock the repository to return a bin with incompatible zone attributes
		Mockito.when(mockedBinRepository.findByCodeAndWarehouseCode(binCode, warehouseCode))
				.thenReturn(Optional.of(binProjection));

		Set<ProductAttribute> productAttributes = Set.of();
		
		Optional<BinService.BinValidationResult> result = binService.validateBin(binCode, warehouseCode,
				productAttributes);
		assertThat(result).isPresent();
		assertThat(result.get().hasWarning()).isFalse();
		assertThat(result.get().warningReason()).isEmpty();
	}
}
