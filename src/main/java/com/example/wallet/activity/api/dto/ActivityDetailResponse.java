package com.example.wallet.activity.api.dto;

import com.example.wallet.activity.domain.ActivityStatus;
import com.example.wallet.activity.domain.ActivityType;
import com.example.wallet.activity.domain.ProductType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;

/**
 * Detailed view of a single activity event.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityDetailResponse
{

	private String id;
	private String userId;

	private ProductType product;
	private ActivityType type;
	private ActivityStatus status;

	private MonetaryAmount amount;

	private Instant occurredAt;
	private Instant createdAt;

	private Map<String, Object> metadata;
}
