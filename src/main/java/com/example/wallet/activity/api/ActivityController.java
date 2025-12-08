package com.example.wallet.activity.api;

import com.example.wallet.activity.api.dto.ActivityDetailResponse;
import com.example.wallet.activity.api.dto.ActivityFeedResponse;
import com.example.wallet.activity.domain.ActivityStatus;
import com.example.wallet.activity.domain.ProductType;
import com.example.wallet.activity.service.ActivityFeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

/**
 * REST controller exposing the unified activity feed APIs.
 */
@RestController
@RequestMapping("/api/v1/activity")
@RequiredArgsConstructor
public class ActivityController
{

	private final ActivityFeedService service;

	@GetMapping
	public ActivityFeedResponse getActivityFeed(@RequestParam String userId,
			@RequestParam(required = false) ProductType product, @RequestParam(required = false) ActivityStatus status,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
			@RequestParam(required = false) String search, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size)
	{
		return service.getActivityFeed(userId, product, status, from, to, search, page, size);
	}

	@GetMapping("/{id}")
	public ActivityDetailResponse getActivityDetail(@PathVariable String id)
	{
		return service.getActivityDetail(id);
	}
}
