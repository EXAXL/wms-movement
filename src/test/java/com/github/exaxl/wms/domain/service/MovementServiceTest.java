package com.github.exaxl.wms.domain.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import com.github.exaxl.wms.adapter.out.db.entity.MovementNaturalId;
import com.github.exaxl.wms.adapter.out.db.repository.MockedMovementRepository;
import com.github.exaxl.wms.domain.exception.DuplicateMovementException;

@ExtendWith(OutputCaptureExtension.class)
class MovementServiceTest {

	private MockedMovementRepository mockedMovementRepository = Mockito.mock(MockedMovementRepository.class);
	private MovementService service = new MovementService(mockedMovementRepository);

	@Nested
	class ensureNotDuplicate {

		@Test
		void handleDuplicate_WhenMovementAlreadyExists_ThenThrowDuplicateMovementException() {
			String movementId = "movement123";
			int whCode = 1;
			Mockito.when(mockedMovementRepository
					.existsByMovementIdAndWarehouseCode(new MovementNaturalId(movementId, whCode))).thenReturn(true);

			Assertions.assertThatThrownBy(() -> service.ensureNotDuplicate(movementId, whCode))
					.isInstanceOf(DuplicateMovementException.class).hasMessageContaining(
							"Duplicate movement detected - movementId: %s, warehouseCode: %s", movementId, whCode);
		}

		@Test
		void handleDuplicate_WhenMovementAlreadyExists_ThenLogWarning(CapturedOutput output) {
			String movementId = "movement123";
			int whCode = 1;
			Mockito.when(mockedMovementRepository
					.existsByMovementIdAndWarehouseCode(new MovementNaturalId(movementId, whCode))).thenReturn(true);

			Assertions.assertThatThrownBy(() -> service.ensureNotDuplicate(movementId, whCode))
					.isInstanceOf(DuplicateMovementException.class);

			assertThat(output).contains(String.format("Duplicate movement detected - movementId: %s, warehouseCode: %s",
					movementId, whCode));
		}

		@Test
		void handleDuplicate_WhenMovementDoesNotExist_ThenDoNothing() {
			String movementId = "movement123";
			int whCode = 1;
			Mockito.when(mockedMovementRepository
					.existsByMovementIdAndWarehouseCode(new MovementNaturalId(movementId, whCode))).thenReturn(false);

			service.ensureNotDuplicate(movementId, whCode);

			Mockito.verify(mockedMovementRepository)
					.existsByMovementIdAndWarehouseCode(new MovementNaturalId(movementId, whCode));
		}
	}

}
