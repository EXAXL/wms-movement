package com.github.exaxl.wms.config.kafka;

import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.kafka.autoconfigure.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;

import com.github.exaxl.wms.adapter.in.messaging.dto.MovementMessageDto;

import lombok.Generated;

@Configuration
@EnableKafka
@Generated
public class KafkaConsumerConfig {

	private static final String TRUSTED_PACKAGES = "com.github.exaxl.wms";

	@Bean
	ConsumerFactory<String, MovementMessageDto> movementConsumerFactory(KafkaProperties kafkaProperties) {

		Map<String, Object> props = kafkaProperties.buildConsumerProperties();

		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
		props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JacksonJsonDeserializer.class);
		props.put(JacksonJsonDeserializer.VALUE_DEFAULT_TYPE, MovementMessageDto.class.getName());
		props.put(JacksonJsonDeserializer.TRUSTED_PACKAGES, TRUSTED_PACKAGES);

		return new DefaultKafkaConsumerFactory<>(props);
	}

	@Bean(name = "movementListenerFactory")
	ConcurrentKafkaListenerContainerFactory<String, MovementMessageDto> movementListenerFactory(
			ConsumerFactory<String, MovementMessageDto> customerConsumerFactory, DefaultErrorHandler errorHandler) {

		ConcurrentKafkaListenerContainerFactory<String, MovementMessageDto> factory = new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(customerConsumerFactory);
		factory.setConcurrency(1);
		factory.setCommonErrorHandler(errorHandler);
	    factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);
		return factory;
	}
}
