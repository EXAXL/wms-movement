package com.github.exaxl.wms.config.kafka.dlt;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.Consumer;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
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
	void configureDeadLetterPublishingContainerFactory_WhenCalled_ThenSetsDeadLetterPublisherCreator() {
		DeadLetterPublishingRecovererFactory factory = Mockito.mock(DeadLetterPublishingRecovererFactory.class);

		Consumer<DeadLetterPublishingRecovererFactory> consumer = service.configureDeadLetterPublishingContainerFactory();
		assertThat(consumer).isNotNull();

		consumer.accept(factory);

		Mockito.verify(factory).setDeadLetterPublisherCreator(Mockito.any());
	}
}
