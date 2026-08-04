package com.github.exaxl.wms.config.kafka.dlt;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.listener.ListenerExecutionFailedException;

import com.github.exaxl.wms.domain.exception.BinNotFoundException;
import com.github.exaxl.wms.domain.exception.WarehouseNotActiveException;

class DltHeaderEnricherTest {

	private static final String APP_NAME = "wms-movement";

	@Test
	void enrichDltRecord_WhenPlainRuntimeException_ThenRetryableHeaderIsTrue() {
		ListenerExecutionFailedException ex = new ListenerExecutionFailedException("wrapped", new RuntimeException("boom"));
		Headers headers = new RecordHeaders();

		DltHeaderEnricher.enrichDltRecord(ex, APP_NAME, headers);

		assertThat(headerValue(headers, DltHeaderEnricher.DLT_ERRROR_RETRYABLE_HEADER)).isEqualTo("true");
	}

	@Test
	void enrichDltRecord_WhenErrorCodeExceptionNotRetryable_ThenRetryableHeaderIsFalse() {
		ListenerExecutionFailedException ex = new ListenerExecutionFailedException("wrapped", new BinNotFoundException());
		Headers headers = new RecordHeaders();

		DltHeaderEnricher.enrichDltRecord(ex, APP_NAME, headers);

		assertThat(headerValue(headers, DltHeaderEnricher.DLT_ERRROR_RETRYABLE_HEADER)).isEqualTo("false");
	}

	@Test
	void enrichDltRecord_WhenErrorCodeExceptionAndRetryable_ThenRetryableHeaderIsTrue() {
		ListenerExecutionFailedException ex = new ListenerExecutionFailedException("wrapped", new WarehouseNotActiveException());
		Headers headers = new RecordHeaders();

		DltHeaderEnricher.enrichDltRecord(ex, APP_NAME, headers);

		assertThat(headerValue(headers, DltHeaderEnricher.DLT_ERRROR_RETRYABLE_HEADER)).isEqualTo("true");
	}

	@Test
	void enrichDltRecord_WhenCalled_ThenServiceHeaderMatchesApplicationName() {
		ListenerExecutionFailedException ex = new ListenerExecutionFailedException("wrapped", new RuntimeException());
		Headers headers = new RecordHeaders();

		DltHeaderEnricher.enrichDltRecord(ex, APP_NAME, headers);

		assertThat(headerValue(headers, DltHeaderEnricher.DLT_SERVICE_HEADER)).isEqualTo(APP_NAME);
	}

	@Test
	void unwrap_WhenNoCauseChain_ThenReturnsOriginalThrowable() {
		RuntimeException ex = new RuntimeException("root");

		Throwable result = DltHeaderEnricher.unwrap(ex);

		assertThat(result).isSameAs(ex);
	}

	@Test
	void unwrap_WhenErrorCodeExceptionNestedInChain_ThenReturnsErrorCodeException() {
		BinNotFoundException errorCodeEx = new BinNotFoundException();
		RuntimeException wrapper = new RuntimeException("outer", errorCodeEx);

		Throwable result = DltHeaderEnricher.unwrap(wrapper);

		assertThat(result).isSameAs(errorCodeEx);
	}

	@Test
	void unwrap_WhenChainHasNoErrorCodeException_ThenReturnsOriginalThrowable() {
		RuntimeException inner = new RuntimeException("inner");
		RuntimeException outer = new RuntimeException("outer", inner);

		Throwable result = DltHeaderEnricher.unwrap(outer);

		assertThat(result).isSameAs(outer);
	}

	private String headerValue(Headers headers, String key) {
		return new String(headers.lastHeader(key).value());
	}
}
