package com.example.wallet.activity.integration.kafka;

import com.example.wallet.activity.domain.ActivityStatus;
import com.example.wallet.activity.domain.ActivityType;
import com.example.wallet.activity.domain.ProductType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

/**
 * Message published by product services into the activity topic.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletActivityEventMessage
{

	private String userId;
	private ProductType product;
	private ActivityType type;
	private BigDecimal amount;
	private String currency;
	private ActivityStatus status;
	private Instant occurredAt;
	private Map<String, Object> metadata;
	// Idempotency key (optional). If not present, we fallback to metadata.transactionId.
	private String externalId;
}
