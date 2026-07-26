package com.github.exaxl.wms.config.kafka.dlt;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ListenerExecutionFailedException;
import org.springframework.kafka.support.serializer.DeserializationException;

import com.github.exaxl.wms.adapter.in.messaging.enums.MessageErrorCode;
import com.github.exaxl.wms.adapter.in.messaging.exception.MessageValidationException;

import tools.jackson.databind.ObjectMapper;

class KafkaDltRecordPublisherTest {

	private static final String TOPIC = "movement-topic";
	private static final String APPLICATION_NAME = "wms-movement";
	private static final String SAMPLE_PAYLOAD = "{\"movementId\":\"MOV-001\"}";

	private KafkaTemplate<String, String> dltKafkaTemplate;

	private KafkaDltRecordPublisher publisher;

	@BeforeEach
	@SuppressWarnings("unchecked")
	void setUp() {
		dltKafkaTemplate = Mockito.mock(KafkaTemplate.class);
		publisher = new KafkaDltRecordPublisher(dltKafkaTemplate, APPLICATION_NAME, new ObjectMapper());
	}

	@Test
	void publish_WhenValidationException_ThenSendsToDltWithCorrectHeaders() {
		MessageValidationException cause = new MessageValidationException("qty must be greater than 0");
		ListenerExecutionFailedException ex = new ListenerExecutionFailedException("wrapped", cause);
		ConsumerRecord<String, String> consumedRecord = new ConsumerRecord<>(TOPIC, 0, 0, "key", SAMPLE_PAYLOAD);

		
		publisher.publish(consumedRecord, ex);

		ArgumentCaptor<ProducerRecord<String, String>> captor = ArgumentCaptor.captor();
		Mockito.verify(dltKafkaTemplate).send(captor.capture());

		ProducerRecord<String, String> dltRecord = captor.getValue();

		assertThat(dltRecord.topic()).isEqualTo(TOPIC + KafkaDltRecordPublisher.DLT_SUFFIX);
		assertThat(dltRecord.value()).isEqualTo(SAMPLE_PAYLOAD);

		assertThat(new String(dltRecord.headers().lastHeader(KafkaDltRecordPublisher.DLT_ERROR_CODE_HEADER).value()))
				.isEqualTo(MessageErrorCode.MESSAGE_VALIDATION_FAILED.getCode());
		assertThat(new String(dltRecord.headers().lastHeader(KafkaDltRecordPublisher.DLT_ERROR_MESSAGE_HEADER).value()))
				.isEqualTo("qty must be greater than 0");
		assertThat(new String(dltRecord.headers().lastHeader(KafkaDltRecordPublisher.DLT_SERVICE_HEADER).value()))
				.isEqualTo(APPLICATION_NAME);
	}

	@Test
	void publish_WhenDeserializationException_ThenSendsToDltWithParsingFailedCode() {
		byte[] originalBytes = SAMPLE_PAYLOAD.getBytes(StandardCharsets.UTF_8);
		DeserializationException cause = new DeserializationException("error", originalBytes, false,
				new RuntimeException());
		ListenerExecutionFailedException ex = new ListenerExecutionFailedException("wrapped", cause);
		ConsumerRecord<String, String> consumedRecord = new ConsumerRecord<>(TOPIC, 0, 0, "key", null);

		publisher.publish(consumedRecord, ex);

		ArgumentCaptor<ProducerRecord<String, String>> captor = ArgumentCaptor.captor();
		Mockito.verify(dltKafkaTemplate).send(captor.capture());

		ProducerRecord<String, String> dltRecord = captor.getValue();

		assertThat(dltRecord.topic()).isEqualTo(TOPIC + KafkaDltRecordPublisher.DLT_SUFFIX);
		assertThat(dltRecord.value()).isEqualTo(SAMPLE_PAYLOAD);
		assertThat(new String(dltRecord.headers().lastHeader(KafkaDltRecordPublisher.DLT_ERROR_CODE_HEADER).value()))
				.isEqualTo(MessageErrorCode.PARSING_FAILED.getCode());
	}

	@Test
	void publish_WhenKeyIsNull_ThenSendsToDltWithNullKey() {
		ListenerExecutionFailedException ex = new ListenerExecutionFailedException("wrapped", new RuntimeException());
		ConsumerRecord<String, String> consumedRecord = new ConsumerRecord<>(TOPIC, 0, 0, null, SAMPLE_PAYLOAD);

		publisher.publish(consumedRecord, ex);

		ArgumentCaptor<ProducerRecord<String, String>> captor = ArgumentCaptor.captor();
		Mockito.verify(dltKafkaTemplate).send(captor.capture());

		assertThat(captor.getValue().key()).isNull();
	}
}
