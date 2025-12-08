package com.example.wallet.activity.integration.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Kafka producer used to publish wallet activity events for testing.
 */
@Component
public class ActivityEventProducer
{

	private final KafkaTemplate<String, String> kafkaTemplate;
	private final ObjectMapper objectMapper;
	private final String topicName = "wallet.activity.events";

	// Use the Spring-managed ObjectMapper so Java Time types are properly supported
	public ActivityEventProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper)
	{
		this.kafkaTemplate = kafkaTemplate;
		this.objectMapper = objectMapper;
	}

	public void publish(WalletActivityEventMessage message)
	{
		try
		{
			String payload = objectMapper.writeValueAsString(message);
			kafkaTemplate.send(topicName, payload);
		}
		catch (JsonProcessingException e)
		{
			throw new IllegalStateException("Failed to serialize activity event message", e);
		}
	}
}
