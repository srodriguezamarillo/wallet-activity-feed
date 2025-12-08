package com.example.wallet.activity.service;

import com.example.wallet.activity.api.dto.ActivityDetailResponse;
import com.example.wallet.activity.api.dto.ActivityFeedItem;
import com.example.wallet.activity.api.dto.ActivityFeedResponse;
import com.example.wallet.activity.api.dto.MonetaryAmount;
import com.example.wallet.activity.domain.ActivityEvent;
import com.example.wallet.activity.domain.ActivityStatus;
import com.example.wallet.activity.domain.ProductType;
import com.example.wallet.activity.repository.ActivityEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Default implementation of the activity feed service.
 */
@Service
@RequiredArgsConstructor
public class ActivityFeedServiceImpl implements ActivityFeedService
{

	private final ActivityEventRepository repository;

	private static final DateTimeFormatter SUBTITLE_FORMATTER =
			DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm").withZone(ZoneId.of("UTC"));

	@Override
	@Cacheable(value = "activityFeed", condition = "#page == 0")
	public ActivityFeedResponse getActivityFeed(String userId, ProductType product, ActivityStatus status, Instant from,
			Instant to, String search, int page, int size)
	{
		PageRequest pageRequest = PageRequest.of(page, size);
		Page<ActivityEvent> result =
				repository.searchFeed(userId, product, status, from, to, normalizeSearch(search), pageRequest);

		return ActivityFeedResponse.builder().items(result.stream().map(this::toFeedItem).toList())
				.page(result.getNumber()).size(result.getSize()).totalElements(result.getTotalElements())
				.totalPages(result.getTotalPages()).hasNext(result.hasNext()).build();
	}

	@Override
	public ActivityDetailResponse getActivityDetail(String id)
	{
		ActivityEvent event =
				repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Activity not found: " + id));

		return ActivityDetailResponse.builder().id(event.getId()).userId(event.getUserId()).product(event.getProduct())
				.type(event.getType()).status(event.getStatus()).occurredAt(event.getOccurredAt())
				.createdAt(event.getCreatedAt())
				.amount(MonetaryAmount.builder().currency(event.getCurrency()).value(event.getAmount()).build())
				.metadata(event.getMetadata()).build();
	}

	private ActivityFeedItem toFeedItem(ActivityEvent event)
	{
		String title = resolveTitle(event);
		String subtitle = resolveSubtitle(event);

		boolean incoming = event.getAmount() != null && event.getAmount().signum() > 0;

		return ActivityFeedItem.builder().id(event.getId()).product(event.getProduct()).type(event.getType())
				.status(event.getStatus()).title(title).subtitle(subtitle).incoming(incoming)
				.primaryAmount(MonetaryAmount.builder().currency(event.getCurrency()).value(event.getAmount()).build())
				.occurredAt(event.getOccurredAt()).build();
	}

	private String resolveTitle(ActivityEvent event)
	{
		Object merchantName = event.getMetadata() != null ? event.getMetadata().get("merchantName") : null;
		Object peerName = event.getMetadata() != null ? event.getMetadata().get("peerName") : null;
		Object source = event.getMetadata() != null ? event.getMetadata().get("source") : null;

		if (merchantName != null)
		{
			return merchantName.toString();
		}
		if (peerName != null)
		{
			return peerName.toString();
		}
		if (source != null)
		{
			return source.toString();
		}
		return event.getType().name();
	}

	private String resolveSubtitle(ActivityEvent event)
	{
		String productLabel = event.getProduct().name();
		String dateLabel = SUBTITLE_FORMATTER.format(event.getOccurredAt());
		return productLabel + " · " + dateLabel;
	}

	private String normalizeSearch(String search)
	{
		if (search == null || search.isBlank())
		{
			return null;
		}
		return search.trim();
	}
}
