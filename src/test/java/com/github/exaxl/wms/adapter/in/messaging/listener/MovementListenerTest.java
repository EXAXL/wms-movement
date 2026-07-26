package com.github.exaxl.wms.adapter.in.messaging.listener;

import org.assertj.core.api.Assertions;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.github.exaxl.wms.adapter.in.messaging.dto.MovementMessageDto;
import com.github.exaxl.wms.adapter.in.messaging.exception.MessageValidationException;
import com.github.exaxl.wms.adapter.in.messaging.handler.MovementErrorHandler;
import com.github.exaxl.wms.adapter.in.messaging.validator.MovementMessageValidator;
import com.github.exaxl.wms.domain.exception.WarehouseNotFoundException;
import com.github.exaxl.wms.domain.usecase.registermovement.RegisterMovement;

class MovementListenerTest {

	private static final String VERSION = "1";
	
	private MovementMessageValidator validator = Mockito.mock(MovementMessageValidator.class);
	private RegisterMovement registerMovement = Mockito.mock(RegisterMovement.class);
	private MovementErrorHandler movementErrorHandler = Mockito.mock(MovementErrorHandler.class);
	private MovementListener service = new MovementListener(validator, registerMovement, movementErrorHandler);
	
	@Test
	void onMessage_WhenValidationFails_ThenManageValidationException() {
		MovementMessageDto message = Instancio.create(MovementMessageDto.class);
		
		MessageValidationException exception = new MessageValidationException("Validation failed");
		Mockito.doThrow(exception).when(validator).validate(message);
		
		Assertions.assertThatThrownBy(() -> service.onMessage(VERSION, message))
			.isInstanceOf(MessageValidationException.class);
		
		Mockito.verify(validator).validate(message);
		Mockito.verify(movementErrorHandler).handleBusinessError(message, exception);
	}

	@Test
	void onMessage_WhenValidationPasses_AndRegisterFails_ThenManageException() {
		MovementMessageDto message = Instancio.create(MovementMessageDto.class);
		
		Mockito.doNothing().when(validator).validate(message);
		
		WarehouseNotFoundException exception = new WarehouseNotFoundException("Warehouse not found");
		Mockito.doThrow(exception).when(registerMovement).register(message);
		
		Assertions.assertThatThrownBy(() -> service.onMessage(VERSION, message))
			.isInstanceOf(WarehouseNotFoundException.class);
		
		Mockito.verify(validator).validate(message);
		Mockito.verify(registerMovement).register(message);
		Mockito.verify(movementErrorHandler).handleBusinessError(message, exception);
		
	}
	
	@Test
	void onMessage_WhenValidationPasses_AndRegisterPasses_ThenManageCorrectlyMovement() {
		MovementMessageDto message = Instancio.create(MovementMessageDto.class);
		
		Mockito.doNothing().when(validator).validate(message);
		Mockito.doNothing().when(registerMovement).register(message);
		
		Assertions.assertThatCode(() -> service.onMessage(VERSION, message))
			.doesNotThrowAnyException();
		
		Mockito.verify(validator).validate(message);
		Mockito.verify(registerMovement).register(message);
		Mockito.verifyNoInteractions(movementErrorHandler);
		
	}
	
	@Test
	void onMessage_WhenUnexpectedExceptionOccurs_ThenPropagateException() {
		MovementMessageDto message = Instancio.create(MovementMessageDto.class);
		
		Mockito.doNothing().when(validator).validate(message);
		
		RuntimeException exception = new RuntimeException("Unexpected error");
		Mockito.doThrow(exception).when(registerMovement).register(message);
		
		Assertions.assertThatThrownBy(() -> service.onMessage(VERSION, message))
			.isInstanceOf(RuntimeException.class)
			.hasMessage("Unexpected error");
		
		Mockito.verify(validator).validate(message);
		Mockito.verify(registerMovement).register(message);
		Mockito.verifyNoInteractions(movementErrorHandler);
		
	}
}
