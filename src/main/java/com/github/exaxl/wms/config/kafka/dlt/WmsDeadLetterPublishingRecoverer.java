package com.github.exaxl.wms.config.kafka.dlt;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.retrytopic.RetryTopicHeaders;
import org.springframework.kafka.support.KafkaHeaders;

import com.github.exaxl.wms.adapter.in.messaging.enums.MessageErrorCode;

public class WmsDeadLetterPublishingRecoverer extends DeadLetterPublishingRecoverer {

	public WmsDeadLetterPublishingRecoverer(
			Function<ProducerRecord<?, ?>, ? extends KafkaOperations<?, ?>> templateResolver,
			BiFunction<ConsumerRecord<?, ?>, Exception, TopicPartition> destinationResolver) {
		super(templateResolver, destinationResolver);
	}

	@Override
	protected ProducerRecord<Object, Object> createProducerRecord(ConsumerRecord<?, ?> consumerRecord,
			TopicPartition topicPartition, Headers headers, byte[] key, byte[] value) {

		// filtering header, remove spring defailt headers if no generic errors
		Optional<Header> customErrorCodeOpt = Stream.of(headers.toArray())
				.filter(header -> DltHeaderEnricher.DLT_ERROR_CODE_HEADER.equalsIgnoreCase(header.key())).findFirst();

		if (customErrorCodeOpt.isPresent()) {
			Header header = customErrorCodeOpt.get();
			if (!MessageErrorCode.GENERIC_ERROR.getCode().equalsIgnoreCase(new String(header.value()))) {
				// remove kafka default headers
				Stream.of(headers.toArray()).filter(h -> h.key().startsWith(KafkaHeaders.PREFIX))
						.forEach(h -> headers.remove(h.key()));
			}
		}

		Optional<Header> retryableErrorCodeOpt = Stream.of(headers.toArray())
				.filter(header -> DltHeaderEnricher.DLT_ERRROR_RETRYABLE_HEADER.equalsIgnoreCase(header.key()))
				.findFirst();
		if (retryableErrorCodeOpt.isPresent()) {
			Header header = retryableErrorCodeOpt.get();
			if (!Boolean.parseBoolean(new String(header.value()))) {
				// remove kafka default headers
				Stream.of(headers.toArray())
						.filter(h -> RetryTopicHeaders.DEFAULT_HEADER_ATTEMPTS.equalsIgnoreCase(h.key())
								|| RetryTopicHeaders.DEFAULT_HEADER_BACKOFF_TIMESTAMP.equalsIgnoreCase(h.key())
								|| RetryTopicHeaders.DEFAULT_HEADER_ORIGINAL_TIMESTAMP.equalsIgnoreCase(h.key()))
						.forEach(h -> headers.remove(h.key()));
			}
		}

		return super.createProducerRecord(consumerRecord, topicPartition, headers, key, value);
	}
}
