package com.github.exaxl.wms.adapter.in.messaging.exception;

import com.github.exaxl.error.exception.core.ValidationException;
import com.github.exaxl.wms.adapter.in.messaging.enums.MessageErrorCode;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Generated;

@SuppressWarnings("java:S110")
@Generated
public class MessageValidationException extends ValidationException{

	private static final long serialVersionUID = 6303074032358384218L;

    public MessageValidationException() {
        super(MessageErrorCode.MESSAGE_VALIDATION_FAILED);
    }
	
	public MessageValidationException(@NotNull Throwable cause) {
		super(MessageErrorCode.MESSAGE_VALIDATION_FAILED, cause);
	}

	public MessageValidationException(@NotEmpty String message) {
		super(message, MessageErrorCode.MESSAGE_VALIDATION_FAILED);
	}

	public MessageValidationException(@NotEmpty String message, @NotNull Throwable cause) {
		super(message, MessageErrorCode.MESSAGE_VALIDATION_FAILED, cause);
	}
}
