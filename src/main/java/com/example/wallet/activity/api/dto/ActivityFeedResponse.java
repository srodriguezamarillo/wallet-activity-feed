package com.example.wallet.activity.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Paged response for the activity feed endpoint.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityFeedResponse
{

	private List<ActivityFeedItem> items;
	private int page;
	private int size;
	private long totalElements;
	private int totalPages;
	private boolean hasNext;
}
