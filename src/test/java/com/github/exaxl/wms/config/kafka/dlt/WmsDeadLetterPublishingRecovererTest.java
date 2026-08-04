package com.github.exaxl.wms.config.kafka.dlt;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.retrytopic.RetryTopicHeaders;
import org.springframework.kafka.support.KafkaHeaders;

import com.github.exaxl.wms.adapter.in.messaging.enums.MessageErrorCode;

class WmsDeadLetterPublishingRecovererTest {

	private static final String TOPIC = "movement-topic";

	@SuppressWarnings("unchecked")
	private final KafkaOperations<?, ?> kafkaOperations = Mockito.mock(KafkaOperations.class);

	private final WmsDeadLetterPublishingRecoverer service = new WmsDeadLetterPublishingRecoverer(
			record -> kafkaOperations,
			(record, ex) -> new TopicPartition(TOPIC, 0));

	@Test
	void createProducerRecord_WhenNoCustomHeaders_ThenKafkaHeadersAreNotRemoved() {
		ConsumerRecord<String, String> consumerRecord = new ConsumerRecord<>(TOPIC, 0, 0, "key", "payload");
		Headers headers = new RecordHeaders();
		headers.add(new RecordHeader(KafkaHeaders.PREFIX + "some-header", "value".getBytes()));

		ProducerRecord<Object, Object> result = service.createProducerRecord(
				consumerRecord, new TopicPartition(TOPIC, 0), headers, null, "payload".getBytes());

		List<String> headerKeys = Arrays.stream(result.headers().toArray()).map(h -> h.key()).toList();
		assertThat(headerKeys).contains(KafkaHeaders.PREFIX + "some-header");
	}

	@Test
	void createProducerRecord_WhenGenericErrorCode_ThenKafkaHeadersAreNotRemoved() {
		ConsumerRecord<String, String> consumerRecord = new ConsumerRecord<>(TOPIC, 0, 0, "key", "payload");
		Headers headers = new RecordHeaders();
		headers.add(new RecordHeader(DltHeaderEnricher.DLT_ERROR_CODE_HEADER,
				MessageErrorCode.GENERIC_ERROR.getCode().getBytes()));
		headers.add(new RecordHeader(KafkaHeaders.PREFIX + "some-header", "value".getBytes()));

		ProducerRecord<Object, Object> result = service.createProducerRecord(
				consumerRecord, new TopicPartition(TOPIC, 0), headers, null, "payload".getBytes());

		List<String> headerKeys = Arrays.stream(result.headers().toArray()).map(h -> h.key()).toList();
		assertThat(headerKeys).contains(KafkaHeaders.PREFIX + "some-header");
	}

	@Test
	void createProducerRecord_WhenNonGenericErrorCode_ThenKafkaDefaultHeadersAreRemoved() {
		ConsumerRecord<String, String> consumerRecord = new ConsumerRecord<>(TOPIC, 0, 0, "key", "payload");
		Headers headers = new RecordHeaders();
		headers.add(new RecordHeader(DltHeaderEnricher.DLT_ERROR_CODE_HEADER,
				MessageErrorCode.MESSAGE_VALIDATION_FAILED.getCode().getBytes()));
		headers.add(new RecordHeader(KafkaHeaders.PREFIX + "some-header", "value".getBytes()));

		ProducerRecord<Object, Object> result = service.createProducerRecord(
				consumerRecord, new TopicPartition(TOPIC, 0), headers, null, "payload".getBytes());

		List<String> headerKeys = Arrays.stream(result.headers().toArray()).map(h -> h.key()).toList();
		assertThat(headerKeys).doesNotContain(KafkaHeaders.PREFIX + "some-header");
	}

	@Test
	void createProducerRecord_WhenRetryableTrue_ThenRetryHeadersAreNotRemoved() {
		ConsumerRecord<String, String> consumerRecord = new ConsumerRecord<>(TOPIC, 0, 0, "key", "payload");
		Headers headers = new RecordHeaders();
		headers.add(new RecordHeader(DltHeaderEnricher.DLT_ERRROR_RETRYABLE_HEADER, "true".getBytes()));
		headers.add(new RecordHeader(RetryTopicHeaders.DEFAULT_HEADER_ATTEMPTS, new byte[]{1}));

		ProducerRecord<Object, Object> result = service.createProducerRecord(
				consumerRecord, new TopicPartition(TOPIC, 0), headers, null, "payload".getBytes());

		List<String> headerKeys = Arrays.stream(result.headers().toArray()).map(h -> h.key()).toList();
		assertThat(headerKeys).contains(RetryTopicHeaders.DEFAULT_HEADER_ATTEMPTS);
	}

	@Test
	void createProducerRecord_WhenRetryableFalse_ThenRetryHeadersAreRemoved() {
		ConsumerRecord<String, String> consumerRecord = new ConsumerRecord<>(TOPIC, 0, 0, "key", "payload");
		Headers headers = new RecordHeaders();
		headers.add(new RecordHeader(DltHeaderEnricher.DLT_ERRROR_RETRYABLE_HEADER, "false".getBytes()));
		headers.add(new RecordHeader(RetryTopicHeaders.DEFAULT_HEADER_ATTEMPTS, new byte[]{1}));
		headers.add(new RecordHeader(RetryTopicHeaders.DEFAULT_HEADER_BACKOFF_TIMESTAMP, "123".getBytes()));
		headers.add(new RecordHeader(RetryTopicHeaders.DEFAULT_HEADER_ORIGINAL_TIMESTAMP, "456".getBytes()));

		ProducerRecord<Object, Object> result = service.createProducerRecord(
				consumerRecord, new TopicPartition(TOPIC, 0), headers, null, "payload".getBytes());

		List<String> headerKeys = Arrays.stream(result.headers().toArray()).map(h -> h.key()).toList();
		assertThat(headerKeys).doesNotContain(
				RetryTopicHeaders.DEFAULT_HEADER_ATTEMPTS,
				RetryTopicHeaders.DEFAULT_HEADER_BACKOFF_TIMESTAMP,
				RetryTopicHeaders.DEFAULT_HEADER_ORIGINAL_TIMESTAMP);
	}
}
