package com.example.wallet.activity.integration.kafka;

import com.example.wallet.activity.domain.ActivityEvent;
import com.example.wallet.activity.repository.ActivityEventRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

/**
 * Kafka consumer that ingests wallet activity events into the unified feed.
 */
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.kafka.enabled", havingValue = "true")
@Slf4j
public class ActivityEventKafkaListener
{

	private final ActivityEventRepository repository;
	private final ObjectMapper objectMapper;

	@KafkaListener(
			topics = "${app.kafka.activity-topic:wallet.activity.events}",
			groupId = "${spring.kafka.consumer.group-id:wallet-activity-feed}")
	public void handleActivityEvent(String payload)
	{
		try
		{
			log.debug("Received activity event from Kafka: {}", payload);

			WalletActivityEventMessage message = objectMapper.readValue(payload, WalletActivityEventMessage.class);

			ActivityEvent event = ActivityEvent.builder().id(UUID.randomUUID().toString()).userId(message.getUserId())
					.product(message.getProduct()).type(message.getType()).amount(message.getAmount())
					.currency(message.getCurrency()).status(message.getStatus()).occurredAt(message.getOccurredAt())
					.createdAt(Instant.now()).metadata(message.getMetadata()).build();

			repository.save(event);

			log.info("Ingested activity event - userId: {}, type: {}, product: {}, amount: {} {}",
					message.getUserId(), message.getType(), message.getProduct(),
					message.getAmount(), message.getCurrency());
		}
		catch (Exception ex)
		{
			log.error("Failed to handle activity event from Kafka - payload: {}, error: {}",
					payload, ex.getMessage(), ex);
			// Re-throw to trigger Kafka retry mechanism
			throw new RuntimeException("Failed to process activity event", ex);
		}
	}
}
