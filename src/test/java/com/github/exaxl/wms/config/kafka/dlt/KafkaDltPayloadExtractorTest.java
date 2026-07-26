package com.github.exaxl.wms.config.kafka.dlt;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.listener.ListenerExecutionFailedException;
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
	}

	private ConsumerRecord<String, Object> buildRecord(Object value) {
		return new ConsumerRecord<>(TOPIC, 0, 0, null, value);
	}

	private ListenerExecutionFailedException buildWrapped(Throwable cause) {
		return new ListenerExecutionFailedException("wrapped", cause);
	}
}
