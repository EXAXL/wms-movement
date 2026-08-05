package com.github.exaxl.wms.config.kafka.dlt;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.BiFunction;
import java.util.function.Consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Headers;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.retrytopic.DeadLetterPublishingRecovererFactory;
import org.springframework.kafka.retrytopic.RetryTopicConfigurationSupport.CustomizersConfigurer;

class RetryTopicConfigTest {

	private static final String APP_NAME = "wms-movement";

	private final RetryTopicConfig service = new RetryTopicConfig(APP_NAME);

	@Test
	void configureCustomizers_WhenCalled_ThenCustomizesDeadLetterPublishingRecoverer() {
		CustomizersConfigurer configurer = Mockito.mock(CustomizersConfigurer.class);

		service.configureCustomizers(configurer);

		Mockito.verify(configurer).customizeDeadLetterPublishingRecoverer(Mockito.any());
	}

	@Test
	@SuppressWarnings("unchecked")
	void configureCustomizers_WhenHeadersFunctionInvoked_ThenReturnsEnrichedHeaders() {
		CustomizersConfigurer configurer = Mockito.mock(CustomizersConfigurer.class);
		DeadLetterPublishingRecoverer recoverer = Mockito.mock(DeadLetterPublishingRecoverer.class);

		service.configureCustomizers(configurer);

		ArgumentCaptor<Consumer<DeadLetterPublishingRecoverer>> recovererConsumerCaptor =
				ArgumentCaptor.forClass(Consumer.class);
		Mockito.verify(configurer).customizeDeadLetterPublishingRecoverer(recovererConsumerCaptor.capture());
		recovererConsumerCaptor.getValue().accept(recoverer);

		ArgumentCaptor<BiFunction<ConsumerRecord<?, ?>, Exception, Headers>> headersFunctionCaptor =
				ArgumentCaptor.forClass(BiFunction.class);
		Mockito.verify(recoverer).addHeadersFunction(headersFunctionCaptor.capture());

		ConsumerRecord<String, String> record = new ConsumerRecord<>("topic", 0, 0, null, null);
		Headers result = headersFunctionCaptor.getValue().apply(record, new RuntimeException("test error"));

		assertThat(result).isNotNull();
	}

	@Test
	void configureDeadLetterPublishingContainerFactory_WhenCalled_ThenSetsDeadLetterPublisherCreator() {
		DeadLetterPublishingRecovererFactory factory = Mockito.mock(DeadLetterPublishingRecovererFactory.class);

		Consumer<DeadLetterPublishingRecovererFactory> consumer = service.configureDeadLetterPublishingContainerFactory();
		assertThat(consumer).isNotNull();

		consumer.accept(factory);

		Mockito.verify(factory).setDeadLetterPublisherCreator(Mockito.any());
	}
}
