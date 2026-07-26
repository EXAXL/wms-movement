package com.github.exaxl.wms.adapter.in.messaging.handler;

import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.github.exaxl.error.exception.core.ErrorCodeException;
import com.github.exaxl.wms.adapter.in.messaging.dto.MovementMessageDto;
import com.github.exaxl.wms.domain.exception.WarehouseNotActiveException;
import com.github.exaxl.wms.domain.exception.WarehouseNotFoundException;
import com.github.exaxl.wms.domain.service.DiscardedRetryableMovementService;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

class MovementErrorHandlerTest {

	private DiscardedRetryableMovementService discardedRetryableMovementService = Mockito.mock(DiscardedRetryableMovementService.class);
	private MeterRegistry meterRegistry = Mockito.mock(MeterRegistry.class);
	private MovementErrorHandler service = new MovementErrorHandler(discardedRetryableMovementService, meterRegistry);
	
	@Test
	void handleNonRetryable_ThenIncreaseMeterRegistry() {
		MovementMessageDto message = Instancio.create(MovementMessageDto.class);
		ErrorCodeException e = new WarehouseNotFoundException();
		
		Counter counter = Mockito.mock(Counter.class);
		Mockito.when(meterRegistry.counter("wms.movements.discarded", "errorCode", e.getErrorCode().getCode())).thenReturn(counter);
		
		service.handleNonRetryable(message, e);
		
		Mockito.verify(meterRegistry).counter("wms.movements.discarded", "errorCode", e.getErrorCode().getCode());
		Mockito.verify(counter).increment();
	}

	@Test
	void handleRetryable_ThenSaveDiscarderMovement() {
		MovementMessageDto message = Instancio.create(MovementMessageDto.class);
		ErrorCodeException e = new WarehouseNotActiveException();
		
		service.handleRetryable(message, e);
		
		Mockito.verify(discardedRetryableMovementService).save(message, e);
	}
	
	@Test
	void handleBusinessError_WhenExceptionIsRetryable_ThenSaveDiscardedMovement() {
		MovementMessageDto message = Instancio.create(MovementMessageDto.class);
		ErrorCodeException e = new WarehouseNotActiveException();
		
		service.handleBusinessError(message, e);
		
		Mockito.verify(discardedRetryableMovementService).save(message, e);
	}
	
	@Test
	void handleBusinessError_WhenExceptionIsNotRetryable_ThenIncrementCounter() {
		MovementMessageDto message = Instancio.create(MovementMessageDto.class);
		ErrorCodeException e = new WarehouseNotFoundException();
		
		Counter counter = Mockito.mock(Counter.class);
		Mockito.when(meterRegistry.counter("wms.movements.discarded", "errorCode", e.getErrorCode().getCode())).thenReturn(counter);
		
		service.handleBusinessError(message, e);
		
		Mockito.verify(meterRegistry).counter("wms.movements.discarded", "errorCode", e.getErrorCode().getCode());
	}
}
