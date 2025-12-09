package com.example.wallet.activity.api;

import java.time.Instant;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.wallet.activity.api.dto.ActivityDetailResponse;
import com.example.wallet.activity.api.dto.ActivityFeedResponse;
import com.example.wallet.activity.domain.ActivityStatus;
import com.example.wallet.activity.domain.ProductType;
import com.example.wallet.activity.service.ActivityFeedService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST controller exposing the unified activity feed APIs.
 */
@RestController
@RequestMapping("/api/v1/activity")
@RequiredArgsConstructor
@Slf4j
public class ActivityController
{
	
	private final ActivityFeedService service;
	
	@GetMapping
	public ActivityFeedResponse getActivityFeed(@RequestParam String userId,
			@RequestParam(required = false) ProductType product, @RequestParam(required = false) ActivityStatus status,
			@RequestParam(required = false) String currency,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
			@RequestParam(required = false) String search, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size)
	{
		log.debug("GET /api/v1/activity - userId: {}, product: {}, status: {}, currency: {}, page: {}, size: {}",
				userId, product, status, currency, page, size);
		
		return service.getActivityFeed(userId, product, status, currency, from, to, search, page, size);
	}
	
	@GetMapping("/{id}")
	public ActivityDetailResponse getActivityDetail(@PathVariable String id)
	{
		log.debug("GET /api/v1/activity/{}", id);
		
		return service.getActivityDetail(id);
	}
}
