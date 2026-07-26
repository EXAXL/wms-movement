package com.github.exaxl.wms.config.kafka.dlt;

import org.apache.kafka.common.header.Headers;

import com.github.exaxl.error.exception.core.ErrorCodeException;
import com.github.exaxl.wms.adapter.in.messaging.exception.DltErrorInfo;
import com.github.exaxl.wms.domain.exception.RetryableError;

public class DltHeaderEnricher {

	protected static final String DLT_ERROR_MESSAGE_HEADER = "dlt-error-message";
	protected static final String DLT_ERROR_CODE_HEADER = "dlt-error-code";
	protected static final String DLT_SERVICE_HEADER = "dlt-service";
	protected static final String DLT_ERRROR_RETRYABLE_HEADER = "dlt-error-retryable";

	public static void enrichDltRecord(Exception exception, String applicationName, Headers headers) {

		DltErrorInfo errorInfo = KafkaDltErrorResolver.resolveDltErrorInfo(exception);
		headers.add(DLT_ERROR_CODE_HEADER, errorInfo.code().getBytes());
		headers.add(DLT_ERROR_MESSAGE_HEADER, errorInfo.message().getBytes());
		headers.add(DLT_SERVICE_HEADER, applicationName.getBytes());

		Throwable cause = unwrap(exception);
		boolean isRetryable = !(cause instanceof ErrorCodeException)
				|| (cause instanceof ErrorCodeException && cause instanceof RetryableError);
		headers.add(DLT_ERRROR_RETRYABLE_HEADER, Boolean.toString(isRetryable).getBytes());
	}

	protected static Throwable unwrap(Throwable throwable) {
		Throwable current = throwable;
		while (current.getCause() != null) {
			current = current.getCause();
			if (current instanceof ErrorCodeException) {
				return current;
			}
		}
		return throwable;
	}

	private DltHeaderEnricher() {
	}
}
