package com.github.exaxl.wms.config.kafka.dlt;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.listener.ListenerExecutionFailedException;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.serializer.DeserializationException;

import tools.jackson.databind.ObjectMapper;

class KafkaDltPayloadExtractorTest {

	private static final String TOPIC = "movement-topic";
	private static final String EXAMPLE_MESSAGE = "{\"movementId\":\"MOV-001\"}";
	private final ObjectMapper objectMapper = new ObjectMapper();

	@Nested
	class Extract {
		@Test
		void extract_WhenDeserializationExceptionWithBytes_ThenReturnsUtf8String() {
			byte[] originalBytes = EXAMPLE_MESSAGE.getBytes(StandardCharsets.UTF_8);
			DeserializationException cause = new DeserializationException("error", originalBytes, false,
					new RuntimeException());

			String result = KafkaDltPayloadExtractor.extract(buildRecord(null), buildWrapped(cause), objectMapper);

			assertThat(result).isEqualTo(EXAMPLE_MESSAGE);
		}

		@Test
		void extract_WhenDeserializationExceptionWithEmptyBytes_ThenReturnsUnableToDeserializePayload() {
			DeserializationException cause = new DeserializationException("error", new byte[] {}, false,
					new RuntimeException());

			String result = KafkaDltPayloadExtractor.extract(buildRecord(null), buildWrapped(cause), objectMapper);

			assertThat(result).isEqualTo("Unable to deserialize payload");
		}

		@Test
		void extract_WhenRecordValueIsString_ThenReturnsStringUnchanged() {
			String result = KafkaDltPayloadExtractor.extract(buildRecord(EXAMPLE_MESSAGE),
					buildWrapped(new RuntimeException()), objectMapper);

			assertThat(result).isEqualTo(EXAMPLE_MESSAGE);
		}

		@Test
		void extract_WhenRecordValueIsByteArray_ThenReturnsUtf8String() {
			byte[] bytes = EXAMPLE_MESSAGE.getBytes(StandardCharsets.UTF_8);

			String result = KafkaDltPayloadExtractor.extract(buildRecord(bytes), buildWrapped(new RuntimeException()),
					objectMapper);

			assertThat(result).isEqualTo(EXAMPLE_MESSAGE);
		}

		@Test
		void extract_WhenRawDataHeaderPresentAndNonEmpty_ThenReturnsStringFromHeader() {
			byte[] headerBytes = EXAMPLE_MESSAGE.getBytes(StandardCharsets.UTF_8);
			ConsumerRecord<String, Object> record = buildRecordWithRawDataHeader(null, headerBytes);

			String result = KafkaDltPayloadExtractor.extract(record, buildWrapped(new RuntimeException()), objectMapper);

			assertThat(result).isEqualTo(EXAMPLE_MESSAGE);
		}

		@Test
		void extract_WhenExceptionHasNullCauseAndIsNotDeserializationException_ThenFallsBackToRecordValue() {
			RuntimeException exceptionWithNullCause = new RuntimeException("direct exception");

			String result = KafkaDltPayloadExtractor.extract(buildRecord(EXAMPLE_MESSAGE), exceptionWithNullCause,
					objectMapper);

			assertThat(result).isEqualTo(EXAMPLE_MESSAGE);
		}
	}

	@Nested
	class IsKafkaRawDataHeaderPresent {

		@Test
		void isKafkaRawDataHeaderPresent_WhenHeaderIsNull_ThenReturnsFalse() {
			ConsumerRecord<String, Object> record = buildRecord(null);

			boolean result = KafkaDltPayloadExtractor.isKafkaRawDataHeaderPresent(record);

			assertThat(result).isFalse();
		}

		@Test
		void isKafkaRawDataHeaderPresent_WhenHeaderIsPresentButEmpty_ThenReturnsFalse() {
			ConsumerRecord<String, Object> record = buildRecordWithRawDataHeader(null, new byte[]{});

			boolean result = KafkaDltPayloadExtractor.isKafkaRawDataHeaderPresent(record);

			assertThat(result).isFalse();
		}

		@Test
		void isKafkaRawDataHeaderPresent_WhenHeaderIsPresentAndNonEmpty_ThenReturnsTrue() {
			ConsumerRecord<String, Object> record = buildRecordWithRawDataHeader(null,
					EXAMPLE_MESSAGE.getBytes(StandardCharsets.UTF_8));

			boolean result = KafkaDltPayloadExtractor.isKafkaRawDataHeaderPresent(record);

			assertThat(result).isTrue();
		}
	}

	@Nested
	class GetPayloadFromKafkaRawDataHeader {

		@Test
		void getPayloadFromKafkaRawDataHeader_WhenHeaderIsNull_ThenReturnsEmpty() {
			ConsumerRecord<String, Object> record = buildRecord(null);

			assertThat(KafkaDltPayloadExtractor.getPayloadFromKafkaRawDataHeader(record)).isEmpty();
		}

		@Test
		void getPayloadFromKafkaRawDataHeader_WhenHeaderIsPresentButEmpty_ThenReturnsEmpty() {
			ConsumerRecord<String, Object> record = buildRecordWithRawDataHeader(null, new byte[]{});

			assertThat(KafkaDltPayloadExtractor.getPayloadFromKafkaRawDataHeader(record)).isEmpty();
		}

		@Test
		void getPayloadFromKafkaRawDataHeader_WhenHeaderIsPresentAndNonEmpty_ThenReturnsUtf8String() {
			byte[] headerBytes = EXAMPLE_MESSAGE.getBytes(StandardCharsets.UTF_8);
			ConsumerRecord<String, Object> record = buildRecordWithRawDataHeader(null, headerBytes);

			assertThat(KafkaDltPayloadExtractor.getPayloadFromKafkaRawDataHeader(record))
					.contains(EXAMPLE_MESSAGE);
		}
	}

	@Nested
	class GetPayloadFromRecordBytes {

		@Test
		void getPayloadFromRecordBytes_WhenValueIsSerializableObject_ThenReturnsJson() {
			ConsumerRecord<String, Object> record = buildRecord(42);

			assertThat(KafkaDltPayloadExtractor.getPayloadFromRecordBytes(record, objectMapper))
					.contains("42");
		}

		@Test
		void getPayloadFromRecordBytes_WhenObjectMapperFails_ThenReturnsUnableToDeserializePayload() {
			ObjectMapper failingMapper = new ObjectMapper() {
				@Override
				public String writeValueAsString(Object value) {
					throw new RuntimeException("serialization failed");
				}
			};
			ConsumerRecord<String, Object> record = buildRecord(new Object());

			assertThat(KafkaDltPayloadExtractor.getPayloadFromRecordBytes(record, failingMapper))
					.contains(KafkaDltPayloadExtractor.UNABLE_TO_DESERIALIZE_PAYLOAD);
		}

		@Test
		void getPayloadFromRecordBytes_WhenValueIsNull_ThenReturnsNullString() {
			ConsumerRecord<String, Object> record = buildRecord(null);

			assertThat(KafkaDltPayloadExtractor.getPayloadFromRecordBytes(record, objectMapper))
					.as("null value should be serialized as the string 'null' by ObjectMapper")
					.contains("null");
		}
	}

	private ConsumerRecord<String, Object> buildRecord(Object value) {
		return new ConsumerRecord<>(TOPIC, 0, 0, null, value);
	}

	private ConsumerRecord<String, Object> buildRecordWithRawDataHeader(Object value, byte[] headerValue) {
		RecordHeaders headers = new RecordHeaders();
		headers.add(new RecordHeader(KafkaHeaders.RAW_DATA, headerValue));
		return new ConsumerRecord<>(TOPIC, 0, 0, 0L, null, 0, 0, null, value, headers, null);
	}

	private ListenerExecutionFailedException buildWrapped(Throwable cause) {
		return new ListenerExecutionFailedException("wrapped", cause);
	}
}
