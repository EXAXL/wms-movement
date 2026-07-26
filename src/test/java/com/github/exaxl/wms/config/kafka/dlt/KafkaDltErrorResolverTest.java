package com.github.exaxl.wms.config.kafka.dlt;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.kafka.listener.ListenerExecutionFailedException;
import org.springframework.kafka.support.serializer.DeserializationException;
import org.springframework.messaging.converter.MessageConversionException;

import com.github.exaxl.wms.adapter.in.messaging.enums.MessageErrorCode;
import com.github.exaxl.wms.adapter.in.messaging.exception.DltErrorInfo;
import com.github.exaxl.wms.adapter.in.messaging.exception.MessageValidationException;

class KafkaDltErrorResolverTest {

	@Test
	void resolveDltErrorInfo_WhenDeserializationException_ThenReturnsParsingFailed() {
		DeserializationException cause = new DeserializationException("error", new byte[] {}, false,
				new RuntimeException());
		ListenerExecutionFailedException ex = new ListenerExecutionFailedException("wrapped", cause);

		DltErrorInfo result = KafkaDltErrorResolver.resolveDltErrorInfo(ex);

		assertThat(result.code()).isEqualTo(MessageErrorCode.PARSING_FAILED.getCode());
		assertThat(result.message()).isEqualTo(MessageErrorCode.PARSING_FAILED.getDescr());
	}

	@Test
	void resolveDltErrorInfo_WhenMessageValidationExceptionWithCustomMessage_ThenReturnsCustomMessage() {
		MessageValidationException cause = new MessageValidationException("qty must be greater than 0");
		ListenerExecutionFailedException ex = new ListenerExecutionFailedException("wrapped", cause);

		DltErrorInfo result = KafkaDltErrorResolver.resolveDltErrorInfo(ex);

		assertThat(result.code()).isEqualTo(MessageErrorCode.MESSAGE_VALIDATION_FAILED.getCode());
		assertThat(result.message()).isEqualTo("qty must be greater than 0");
	}

	@Test
	void resolveDltErrorInfo_WhenMessageValidationExceptionWithoutCustomMessage_ThenReturnsErrorCodeDescr() {
		MessageValidationException cause = new MessageValidationException(
				MessageErrorCode.MESSAGE_VALIDATION_FAILED.getDescr());
		ListenerExecutionFailedException ex = new ListenerExecutionFailedException("wrapped", cause);

		DltErrorInfo result = KafkaDltErrorResolver.resolveDltErrorInfo(ex);

		assertThat(result.code()).isEqualTo(MessageErrorCode.MESSAGE_VALIDATION_FAILED.getCode());
		assertThat(result.message()).isEqualTo(MessageErrorCode.MESSAGE_VALIDATION_FAILED.getDescr());
	}

	@Test
	void resolveDltErrorInfo_WhenMessageConversionException_ThenReturnsMissingHeader() {
		MessageConversionException cause = new MessageConversionException("header missing");
		ListenerExecutionFailedException ex = new ListenerExecutionFailedException("wrapped", cause);

		DltErrorInfo result = KafkaDltErrorResolver.resolveDltErrorInfo(ex);

		assertThat(result.code()).isEqualTo(MessageErrorCode.MISSING_HEADER.getCode());
		assertThat(result.message()).isEqualTo(MessageErrorCode.MISSING_HEADER.getDescr());
	}

	@Test
	void resolveDltErrorInfo_WhenUnknownException_ThenReturnsGenericError() {
		ListenerExecutionFailedException ex = new ListenerExecutionFailedException("wrapped",
				new RuntimeException("unexpected"));

		DltErrorInfo result = KafkaDltErrorResolver.resolveDltErrorInfo(ex);

		assertThat(result.code()).isEqualTo(MessageErrorCode.GENERIC_ERROR.getCode());
		assertThat(result.message()).isEqualTo(MessageErrorCode.GENERIC_ERROR.getDescr());
	}
}
