package com.github.exaxl.wms.adapter.in.messaging.listener;

import org.springframework.kafka.annotation.BackOff;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import com.github.exaxl.error.exception.core.ErrorCodeException;
import com.github.exaxl.error.exception.core.NotFoundException;
import com.github.exaxl.error.exception.core.ValidationException;
import com.github.exaxl.wms.adapter.in.messaging.dto.MovementMessageDto;
import com.github.exaxl.wms.adapter.in.messaging.handler.MovementErrorHandler;
import com.github.exaxl.wms.adapter.in.messaging.validator.MovementMessageValidator;
import com.github.exaxl.wms.domain.usecase.registermovement.RegisterMovement;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class MovementListener {

	private final MovementMessageValidator validator;
	private final RegisterMovement registerMovement;
	private final MovementErrorHandler movementErrorHandler;

	public MovementListener(MovementMessageValidator validator, RegisterMovement registerMovement,
			MovementErrorHandler movementErrorHandler) {
		this.validator = validator;
		this.registerMovement = registerMovement;
		this.movementErrorHandler = movementErrorHandler;
	}

	// @formatter:off
	@RetryableTopic(attempts = "${wms.kafka.retry.attempts}", 
			backOff = @BackOff(delayString = "${wms.kafka.retry.initial-delay}", multiplierString = "${wms.kafka.retry.multiplier}", maxDelayString = "${wms.kafka.retry.max-delay}"), 
			dltTopicSuffix = "${wms.kafka.dlt.topic-suffix}", 
			exclude = { ErrorCodeException.class },
			topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE,
			kafkaTemplate = "retryTopicKafkaTemplate",
			autoStartDltHandler = "false",
			dltStrategy = DltStrategy.FAIL_ON_ERROR
	)
	// @formatter:on
	@KafkaListener(topics = "${wms.kafka.topics.movements}", containerFactory = "movementListenerFactory", clientIdPrefix = "${spring.kafka.consumer.client-id}")
	public void onMessage(@Header(value = "version") String version, MovementMessageDto message) {

		try {
			log.debug("Message received {}", message);
			validator.validate(message);
			registerMovement.register(message);
		} catch (ValidationException | NotFoundException e) {
			movementErrorHandler.handleBusinessError(message, e);
			throw e;
		}
	}
}
