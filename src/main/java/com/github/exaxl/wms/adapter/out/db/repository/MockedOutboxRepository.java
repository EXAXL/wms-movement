package com.github.exaxl.wms.adapter.out.db.repository;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class MockedOutboxRepository {

	public void save(Object message) {
		log.info("Mocked save operation for message: " + message);
	}
}
