package com.example.wallet.activity.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;

/**
 * Structured error response for API errors.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse
{

	private String error;
	private String message;
	private Instant timestamp;
	private String path;
	private Map<String, Object> details;
}

