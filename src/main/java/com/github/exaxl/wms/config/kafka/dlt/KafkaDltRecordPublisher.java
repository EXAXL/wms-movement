package com.github.exaxl.wms.config.kafka.dlt;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import tools.jackson.databind.ObjectMapper;

/*
 * This class is responsible for publishing records to the Dead Letter Topic (DLT) in Kafka
 * if @RetryableTopic is not used. It extracts the payload from the consumed record and adds relevant headers before sending it to the DLT.
 */
@Component
public class KafkaDltRecordPublisher {

	protected static final String DLT_SUFFIX = ".DLT";
	protected static final String DLT_ERROR_MESSAGE_HEADER = "dlt-error-message";
	protected static final String DLT_ERROR_CODE_HEADER = "dlt-error-code";
	protected static final String DLT_SERVICE_HEADER = "dlt-service";

	private final KafkaTemplate<String, String> dltKafkaTemplate;
	private final String applicationName;
	private final ObjectMapper objectMapper;

	public KafkaDltRecordPublisher(@Qualifier("dltKafkaTemplate") KafkaTemplate<String, String> dltKafkaTemplate,
			@Value("${spring.application.name}") String applicationName, ObjectMapper objectMapper) {
		this.dltKafkaTemplate = dltKafkaTemplate;
		this.applicationName = applicationName;
		this.objectMapper = objectMapper;
	}

	public void publish(ConsumerRecord<?, ?> consumedRecord, Exception exception) {
		String payload = KafkaDltPayloadExtractor.extract(consumedRecord, exception, objectMapper);

		ProducerRecord<String, String> dltRecord = new ProducerRecord<>(consumedRecord.topic() + DLT_SUFFIX,
				consumedRecord.partition(), consumedRecord.key() != null ? consumedRecord.key().toString() : null,
				payload);
		
		// Copy headers from the consumed record to the DLT record
		consumedRecord.headers().forEach(header -> dltRecord.headers().add(header.key(), header.value()));

		// Enrich the DLT record with additional headers related to the error and service
		DltHeaderEnricher.enrichDltRecord(exception, applicationName, dltRecord.headers());

		dltKafkaTemplate.send(dltRecord);
	}
}
