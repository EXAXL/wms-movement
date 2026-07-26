package com.github.exaxl.wms.config.kafka.dlt;

import java.util.Map;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.kafka.autoconfigure.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.JacksonJsonSerializer;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.util.backoff.FixedBackOff;

import lombok.Generated;

@Configuration
@Generated
public class KafkaDltConfig {

	private static final String RETRY_TOPIC_SCHEDULER_THREAD_PREFIX = "retry-topic-scheduler-";
	
	@Bean(name = "dltKafkaTemplate")
	KafkaTemplate<String, String> dltKafkaTemplate(KafkaProperties kafkaProperties) {

		Map<String, Object> props = kafkaProperties.buildProducerProperties();

		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(props));
	}

	@Bean(name = "retryTopicKafkaTemplate")
	KafkaTemplate<String, Object> retryTopicKafkaTemplate(KafkaProperties kafkaProperties) {
	    Map<String, Object> props = kafkaProperties.buildProducerProperties();
	    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
	    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JacksonJsonSerializer.class);
	    return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(props));
	}

	@Bean
	DefaultErrorHandler errorHandler(KafkaDltRecordPublisher publisher) {
	    return new DefaultErrorHandler(
	        publisher::publish,
	        new FixedBackOff(0L, 0L)
	    );
	}
	
	@Bean(name = "retryTopicScheduler")
	TaskScheduler retryTopicTaskScheduler() {
		ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
		scheduler.setPoolSize(1);
		scheduler.setThreadNamePrefix(RETRY_TOPIC_SCHEDULER_THREAD_PREFIX);
		scheduler.initialize();
		return scheduler;
	}
}
