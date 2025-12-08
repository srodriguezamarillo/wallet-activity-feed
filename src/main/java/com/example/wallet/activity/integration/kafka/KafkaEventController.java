package com.example.wallet.activity.integration.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST endpoint used to simulate product events being published to Kafka.
 */
@RestController
@RequestMapping("/api/v1/activity/events")
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.kafka.enabled", havingValue = "true")
public class KafkaEventController
{

	private final ActivityEventProducer producer;

	@PostMapping
	public void publishEvent(@RequestBody WalletActivityEventMessage message)
	{
		producer.publish(message);
	}
}
