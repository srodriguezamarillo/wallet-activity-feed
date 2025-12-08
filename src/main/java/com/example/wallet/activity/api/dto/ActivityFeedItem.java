package com.example.wallet.activity.api.dto;

import com.example.wallet.activity.domain.ActivityStatus;
import com.example.wallet.activity.domain.ActivityType;
import com.example.wallet.activity.domain.ProductType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Lightweight representation of an activity for feed listing.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityFeedItem
{

	private String id;
	private ProductType product;
	private ActivityType type;
	private ActivityStatus status;

	private String title;
	private String subtitle;

	private boolean incoming;
	private MonetaryAmount primaryAmount;

	private Instant occurredAt;
}
