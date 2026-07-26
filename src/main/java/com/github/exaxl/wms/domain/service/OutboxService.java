package com.github.exaxl.wms.domain.service;

import org.springframework.stereotype.Service;

import com.github.exaxl.wms.adapter.out.db.repository.MockedOutboxRepository;
import com.github.exaxl.wms.domain.model.movement.WarehouseMovement;

@Service
public class OutboxService {

    private final MockedOutboxRepository mockedOutboxRepository;
    
    public OutboxService(MockedOutboxRepository mockedOutboxRepository) {
		this.mockedOutboxRepository = mockedOutboxRepository;
	}
    
    public void saveOutboxEvent(WarehouseMovement movement) {
		// TODO Logic to save the outbox message using the mocked repository
		mockedOutboxRepository.save(movement);
	}
}
