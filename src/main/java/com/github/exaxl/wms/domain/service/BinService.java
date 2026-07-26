package com.github.exaxl.wms.domain.service;

import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

import com.github.exaxl.wms.adapter.out.db.repository.MockedBinRepository;
import com.github.exaxl.wms.adapter.out.db.repository.MockedBinRepository.BinValidationProjection;
import com.github.exaxl.wms.domain.exception.BinNotFoundException;
import com.github.exaxl.wms.domain.exception.BinZoneIncompatibleException;
import com.github.exaxl.wms.domain.model.product.ProductAttribute;
import com.github.exaxl.wms.domain.model.product.ProductZoneCompatibilityRules;
import com.github.exaxl.wms.domain.model.warehouse.BinStatus;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BinService {

	private static final String BIN_STATUS_IS_NOT_AVAILABLE = "Bin status is not AVAILABLE";
	private final MockedBinRepository mockedBinRepository;

	public BinService(MockedBinRepository mockedBinRepository) {
		this.mockedBinRepository = mockedBinRepository;
	}

	public Optional<BinValidationResult> validateBin(@Nullable String binCode, int warehouseCode,
			Set<ProductAttribute> productAttributes) {

		if (StringUtils.isBlank(binCode)) {
			return Optional.empty();
		}

		BinValidationProjection bin = mockedBinRepository.findByCodeAndWarehouseCode(binCode, warehouseCode)
				.orElseThrow(() -> new BinNotFoundException("Bin not found: " + binCode));

		// compare product attributes with bin zone attributes
		if (!ProductZoneCompatibilityRules.isCompatible(productAttributes, bin.zoneAttributes())) {
			throw new BinZoneIncompatibleException(
					"Bin zone attributes are not compatible with product attributes for bin: " + binCode);
		}

		// bin status → soft warning, no blocking of the movement, but a warning is
		// logged and returned in the result
		boolean hasWarning = !BinStatus.AVAILABLE.equals(bin.status());
		if (hasWarning) {
			log.warn("Bin {} is not available (status: {}) — movement will be registered with warning", binCode,
					bin.status());
		}

		return Optional.of(new BinValidationResult(bin, !BinStatus.AVAILABLE.equals(bin.status()),
				Optional.ofNullable(hasWarning ? BIN_STATUS_IS_NOT_AVAILABLE : null)));
	}

	public record BinValidationResult(BinValidationProjection bin, boolean hasWarning, Optional<String> warningReason) {
	}
}
