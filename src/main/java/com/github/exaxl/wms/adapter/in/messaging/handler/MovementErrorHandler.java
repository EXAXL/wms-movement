package com.github.exaxl.wms.adapter.in.messaging.handler;

import org.springframework.stereotype.Component;

import com.github.exaxl.error.exception.core.ErrorCodeException;
import com.github.exaxl.wms.adapter.in.messaging.dto.MovementMessageDto;
import com.github.exaxl.wms.domain.exception.RetryableError;
import com.github.exaxl.wms.domain.service.DiscardedRetryableMovementService;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class MovementErrorHandler {

	private static final String MOVEMENT_DISCARDED_COUNTER = "wms.movements.discarded";
	private static final String ERROR_CODE_TAG = "errorCode";
	
	private final DiscardedRetryableMovementService discardedRetryableMovementService;
	private final MeterRegistry meterRegistry;

	public MovementErrorHandler(DiscardedRetryableMovementService discardedRetryableMovementService, MeterRegistry meterRegistry) {
		this.discardedRetryableMovementService = discardedRetryableMovementService;
		this.meterRegistry = meterRegistry;
	}

	public void handleBusinessError(MovementMessageDto message, ErrorCodeException e) {
		if (e instanceof RetryableError) {
			handleRetryable(message, e);
		} else {
			handleNonRetryable(message, e);
		}
	}

	protected void handleRetryable(MovementMessageDto message, ErrorCodeException e) {
		log.warn("Movement discarded (retryable) {} for reason {}", message, e.getMessage());
		discardedRetryableMovementService.save(message, e);
	}

	protected void handleNonRetryable(MovementMessageDto message, ErrorCodeException e) {
		log.warn("Movement discarded (non-retryable) {} for reason {}", message, e.getMessage());
		meterRegistry.counter(MOVEMENT_DISCARDED_COUNTER, ERROR_CODE_TAG, e.getErrorCode().getCode()).increment();
	}
}
