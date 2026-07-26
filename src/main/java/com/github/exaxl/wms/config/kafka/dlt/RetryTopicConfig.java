package com.github.exaxl.wms.config.kafka.dlt;

import java.util.function.Consumer;

import org.apache.kafka.common.header.internals.RecordHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.retrytopic.DeadLetterPublishingRecovererFactory;
import org.springframework.kafka.retrytopic.RetryTopicConfigurationSupport;

@Configuration
public class RetryTopicConfig extends RetryTopicConfigurationSupport {

	private final String applicationName;

	public RetryTopicConfig(@Value("${spring.application.name}") String applicationName) {
		this.applicationName = applicationName;
	}

	@Override
	protected void configureCustomizers(CustomizersConfigurer customizersConfigurer) {
	    customizersConfigurer.customizeDeadLetterPublishingRecoverer(
	            recoverer -> recoverer.addHeadersFunction((consumerRecord, ex) -> {
	            	// add custom headers to the DLT record
	                RecordHeaders headers = new RecordHeaders();
	                DltHeaderEnricher.enrichDltRecord(ex, applicationName, headers); 
	                return headers;
	            }));
	}

	@Override
	protected Consumer<DeadLetterPublishingRecovererFactory> configureDeadLetterPublishingContainerFactory() {
		return factory -> factory.setDeadLetterPublisherCreator(WmsDeadLetterPublishingRecoverer::new);
	}

}
