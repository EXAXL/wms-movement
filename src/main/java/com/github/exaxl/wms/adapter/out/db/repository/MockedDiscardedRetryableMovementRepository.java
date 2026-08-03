package com.github.exaxl.wms.adapter.out.db.repository;

import org.springframework.stereotype.Component;

import com.github.exaxl.error.exception.core.ErrorCodeException;
import com.github.exaxl.wms.adapter.in.messaging.dto.MovementMessageDto;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class MockedDiscardedRetryableMovementRepository {

	public void save(MovementMessageDto message, ErrorCodeException e) {
		// Mock implementation: Log the discarded retryable movement
		log.info("Discarded retryable movement saved: {}, due to error: {}", message, e.getMessage());
	}

}
