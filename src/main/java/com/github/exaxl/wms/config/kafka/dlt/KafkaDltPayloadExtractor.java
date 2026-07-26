package com.github.exaxl.wms.config.kafka.dlt;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.serializer.DeserializationException;

import tools.jackson.databind.ObjectMapper;

public final class KafkaDltPayloadExtractor {

	protected static final String UNABLE_TO_DESERIALIZE_PAYLOAD = "Unable to deserialize payload";

	public static String extract(ConsumerRecord<?, ?> consumedRecord, Exception exception, ObjectMapper objectMapper) {

		Optional<String> recordStringOpt;

		Throwable cause = exception.getCause() != null ? exception.getCause() : exception;
		
		if (cause instanceof DeserializationException de) {
			recordStringOpt = getPayloadFromDeserializationException(de);
		} else {
			
			if(isKafkaRawDataHeaderPresent(consumedRecord)) {
				recordStringOpt = getPayloadFromKafkaRawDataHeader(consumedRecord);
			} else {
				// fall back to the record value
				recordStringOpt = getPayloadFromRecordBytes(consumedRecord, objectMapper);
			}
		}

		return recordStringOpt.orElse(UNABLE_TO_DESERIALIZE_PAYLOAD);
	}

	protected static Optional<String> getPayloadFromDeserializationException(DeserializationException de) {
		byte[] originalBytes = de.getData();
		return originalBytes.length > 0 ? Optional.of(buildStringFromBytes(originalBytes)) : Optional.empty();
	}

	protected static boolean isKafkaRawDataHeaderPresent(ConsumerRecord<?, ?> consumedRecord) {
		Header rawHeader = consumedRecord.headers().lastHeader(KafkaHeaders.RAW_DATA);
		return rawHeader != null && rawHeader.value().length > 0;
	}
	
	protected static Optional<String> getPayloadFromKafkaRawDataHeader(ConsumerRecord<?, ?> consumedRecord) {
		Header rawHeader = consumedRecord.headers().lastHeader(KafkaHeaders.RAW_DATA);
		return rawHeader != null && rawHeader.value().length > 0 ? Optional.of(buildStringFromBytes(rawHeader.value()))
				: Optional.empty();
	}

	protected static Optional<String> getPayloadFromRecordBytes(ConsumerRecord<?, ?> consumedRecord,
			ObjectMapper objectMapper) {
		String recordString = null;

		if (consumedRecord.value() instanceof byte[] bytes) {
			recordString = buildStringFromBytes(bytes);
		} else if (consumedRecord.value() instanceof String str) {
			recordString = str;
		} else {
			try {
				recordString = objectMapper.writeValueAsString(consumedRecord.value());
			} catch (Exception e) {
				recordString = UNABLE_TO_DESERIALIZE_PAYLOAD;
			}
		}

		return Optional.of(recordString);
	}

	private static String buildStringFromBytes(byte[] bytes) {
		return new String(bytes, StandardCharsets.UTF_8);
	}

	private KafkaDltPayloadExtractor() {
	}
}
