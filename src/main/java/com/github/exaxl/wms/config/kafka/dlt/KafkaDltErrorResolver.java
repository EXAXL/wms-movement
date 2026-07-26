package com.github.exaxl.wms.config.kafka.dlt;

import java.util.Map;
import java.util.function.Function;

import org.springframework.kafka.support.serializer.DeserializationException;
import org.springframework.messaging.converter.MessageConversionException;

import com.github.exaxl.error.exception.core.ErrorCodeException;
import com.github.exaxl.wms.adapter.in.messaging.enums.MessageErrorCode;
import com.github.exaxl.wms.adapter.in.messaging.exception.DltErrorInfo;

public class KafkaDltErrorResolver {

	// @formatter:off
	private static final Map<Class<? extends Throwable>, Function<Throwable, DltErrorInfo>> NO_ERRORCODE_EXCEPTION_RESOLVERS = Map.of(
	       
			DeserializationException.class, ex -> new DltErrorInfo(
	            MessageErrorCode.PARSING_FAILED.getCode(),
	            MessageErrorCode.PARSING_FAILED.getDescr()
	        ),
			
	        MessageConversionException.class, ex -> new DltErrorInfo(
	            MessageErrorCode.MISSING_HEADER.getCode(),
	            MessageErrorCode.MISSING_HEADER.getDescr()
	        )
	    );
	// @formatter:on

	public static DltErrorInfo resolveDltErrorInfo(Exception exception) {
		Throwable cause = unwrap(exception);
		
		DltErrorInfo errorInfo;

		if (cause instanceof ErrorCodeException ece) {
			errorInfo = resolveFromErrorCodeException(ece);
		} else {
			errorInfo = NO_ERRORCODE_EXCEPTION_RESOLVERS
					.getOrDefault(cause.getClass(), ex -> new DltErrorInfo(MessageErrorCode.GENERIC_ERROR.getCode(),
							MessageErrorCode.GENERIC_ERROR.getDescr()))
					.apply(cause);
		}

		return errorInfo;
	}

	protected static Throwable unwrap(Throwable throwable) {
	    Throwable current = throwable;
	    while (current.getCause() != null) {
	        current = current.getCause();
	        if (current instanceof ErrorCodeException 
	                || NO_ERRORCODE_EXCEPTION_RESOLVERS.containsKey(current.getClass())) {
	            return current;
	        }
	    }
	    return throwable;
	}
	
	protected static DltErrorInfo resolveFromErrorCodeException(ErrorCodeException ece) {
		boolean customMessage = ece.getMessage() != null
				&& !ece.getMessage().equalsIgnoreCase(ece.getErrorCode().getDescr());

		return new DltErrorInfo(ece.getErrorCode().getCode(),
				customMessage ? ece.getMessage() : ece.getErrorCode().getDescr());
	}

	private KafkaDltErrorResolver() {
	}
}
