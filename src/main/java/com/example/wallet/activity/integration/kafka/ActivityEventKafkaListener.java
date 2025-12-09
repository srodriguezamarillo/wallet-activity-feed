package com.example.wallet.activity.integration.kafka;

import com.example.wallet.activity.domain.ActivityEvent;
import com.example.wallet.activity.repository.ActivityEventRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
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
	private final KafkaTemplate<String, String> kafkaTemplate;

	@Value("${app.kafka.dlq-topic:wallet.activity.events.dlq}")
	private String dlqTopicName;

	@KafkaListener(
			topics = "${app.kafka.activity-topic:wallet.activity.events}",
			groupId = "${spring.kafka.consumer.group-id:wallet-activity-feed}")
	public void handleActivityEvent(String payload)
	{
		try
		{
			log.debug("Received activity event from Kafka: {}", payload);

			WalletActivityEventMessage message = objectMapper.readValue(payload, WalletActivityEventMessage.class);

			String externalId = resolveExternalId(message);

			if (externalId != null && !externalId.isBlank() && repository.findByExternalId(externalId).isPresent())
			{
				log.info("Skipping duplicate activity event - externalId={}, userId={}", externalId,
						message.getUserId());
				return;
			}

			ActivityEvent event = ActivityEvent.builder().id(UUID.randomUUID().toString()).externalId(externalId)
					.userId(message.getUserId()).product(message.getProduct()).type(message.getType())
					.amount(message.getAmount()).currency(message.getCurrency()).status(message.getStatus())
					.occurredAt(message.getOccurredAt()).createdAt(Instant.now()).metadata(message.getMetadata())
					.build();

			repository.save(event);

			log.info("Ingested activity event - userId: {}, type: {}, product: {}, amount: {} {}", message.getUserId(),
					message.getType(), message.getProduct(), message.getAmount(), message.getCurrency());
		}
		catch (Exception ex)
		{
			if (ex instanceof JsonProcessingException || ex instanceof IllegalArgumentException)
			{
				log.error("Invalid activity event payload. Sending to DLQ. payload={}, error={}", payload,
						ex.getMessage());
				sendToDlq(payload, ex.getMessage());
				// Do not rethrow: we handled the poison message and move on
				return;
			}

			log.error("Failed to handle activity event from Kafka - payload: {}, error: {}", payload, ex.getMessage(),
					ex);
			// Rethrow to allow Kafka retry/backoff for transient issues
			throw new RuntimeException("Failed to process activity event", ex);
		}
	}

	/**
	 * Resolves the idempotency key for an incoming event. Priority: 1) explicit
	 * externalId 2) metadata.transactionId (if present)
	 */
	private String resolveExternalId(WalletActivityEventMessage message)
	{
		if (message.getExternalId() != null && !message.getExternalId().isBlank())
		{
			return message.getExternalId();
		}

		if (message.getMetadata() != null)
		{
			Object txId = message.getMetadata().get("transactionId");
			if (txId != null)
			{
				return txId.toString();
			}
		}

		return null;
	}

	/**
	 * Publishes invalid events to a DLQ topic with basic context.
	 */
	private void sendToDlq(String payload, String errorMessage)
	{
		try
		{
			String dlqPayload = objectMapper.writeValueAsString(
					Map.of("payload", payload, "error", errorMessage, "timestamp", Instant.now().toString()));
			kafkaTemplate.send(dlqTopicName, dlqPayload);
		}
		catch (Exception e)
		{
			log.error("Failed to publish message to DLQ - originalPayload={}, error={}", payload, e.getMessage(), e);
		}
	}
}
