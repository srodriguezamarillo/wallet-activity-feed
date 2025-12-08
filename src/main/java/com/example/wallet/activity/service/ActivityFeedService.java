package com.example.wallet.activity.service;

import com.example.wallet.activity.api.dto.ActivityDetailResponse;
import com.example.wallet.activity.api.dto.ActivityFeedResponse;
import com.example.wallet.activity.domain.ActivityStatus;
import com.example.wallet.activity.domain.ProductType;

import java.time.Instant;

/**
 * Service that exposes query operations for the unified activity feed.
 */
public interface ActivityFeedService
{

	ActivityFeedResponse getActivityFeed(String userId, ProductType product, ActivityStatus status, Instant from,
			Instant to, String search, int page, int size);

	ActivityDetailResponse getActivityDetail(String id);
}
