package com.github.exaxl.wms.domain.service;

import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.github.exaxl.wms.adapter.in.messaging.dto.MovementMessageDto;
import com.github.exaxl.wms.adapter.out.db.repository.MockedDiscardedRetryableMovementRepository;
import com.github.exaxl.wms.domain.exception.BinNotFoundException;

class DiscardedRetryableMovementServiceTest {

	private MockedDiscardedRetryableMovementRepository mockedDiscardedRetryableMovementRepository = Mockito
			.mock(MockedDiscardedRetryableMovementRepository.class);
	private DiscardedRetryableMovementService service = new DiscardedRetryableMovementService(mockedDiscardedRetryableMovementRepository);

	@Test
	void save_ShouldDelegateToRepository() {
		MovementMessageDto message = Instancio.create(MovementMessageDto.class);
		BinNotFoundException exception = new BinNotFoundException("Bin not found");
		service.save(message, exception);
		
		Mockito.verify(mockedDiscardedRetryableMovementRepository).save(message, exception);
	}

}
