package com.example.wallet.activity.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.Index;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * Canonical representation of a financial activity in the unified feed.
 */
@Entity
@Table(
		name = "activity_events",
		indexes = { @Index(name = "idx_activity_user_occurred", columnList = "user_id,occurred_at"),
				@Index(name = "idx_activity_product", columnList = "product"),
				@Index(name = "idx_activity_status", columnList = "status"),
				@Index(name = "idx_activity_currency", columnList = "currency") })
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityEvent
{

	@Id
	@Column(name = "id", nullable = false, updatable = false, length = 36)
	@Builder.Default
	private String id = UUID.randomUUID().toString();

	@Column(name = "user_id", nullable = false, length = 64)
	private String userId;

	@Column(name = "external_id", length = 128, unique = true)
	private String externalId;

	@Enumerated(EnumType.STRING)
	@Column(name = "product", nullable = false, length = 32)
	private ProductType product;

	@Enumerated(EnumType.STRING)
	@Column(name = "type", nullable = false, length = 64)
	private ActivityType type;

	@Column(name = "amount", nullable = false, precision = 19, scale = 4)
	private BigDecimal amount;

	@Column(name = "currency", nullable = false, length = 3)
	private String currency;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false, length = 32)
	private ActivityStatus status;

	@Column(name = "occurred_at", nullable = false)
	private Instant occurredAt;

	@Column(name = "created_at", nullable = false)
	private Instant createdAt;

	@Column(name = "updated_at")
	private Instant updatedAt;

	@Convert(converter = MetadataConverter.class)
	@Column(name = "metadata", columnDefinition = "json")
	private Map<String, Object> metadata;
}
