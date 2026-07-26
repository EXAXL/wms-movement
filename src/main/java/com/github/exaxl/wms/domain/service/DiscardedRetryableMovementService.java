package com.github.exaxl.wms.domain.service;

import org.springframework.stereotype.Service;

import com.github.exaxl.error.exception.core.ErrorCodeException;
import com.github.exaxl.wms.adapter.in.messaging.dto.MovementMessageDto;
import com.github.exaxl.wms.adapter.out.db.repository.MockedDiscardedRetryableMovementRepository;

@Service
public class DiscardedRetryableMovementService {

	private final MockedDiscardedRetryableMovementRepository mockedDiscardedRetryableMovementRepository;
	
	public DiscardedRetryableMovementService(MockedDiscardedRetryableMovementRepository mockedDiscardedRetryableMovementRepository) {
		this.mockedDiscardedRetryableMovementRepository = mockedDiscardedRetryableMovementRepository;
	}
	
	public void save(MovementMessageDto message, ErrorCodeException e) {
		mockedDiscardedRetryableMovementRepository.save(message, e);
	}
}
