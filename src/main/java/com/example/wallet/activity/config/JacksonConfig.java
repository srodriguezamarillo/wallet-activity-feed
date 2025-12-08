package com.example.wallet.activity.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Central Jackson configuration for JSON (REST + Kafka).
 */
@Configuration
public class JacksonConfig
{

	@Bean
	public ObjectMapper objectMapper()
	{
		ObjectMapper mapper = new ObjectMapper();
		// Support Java 8 date/time types (Instant, LocalDateTime, etc.)
		mapper.registerModule(new JavaTimeModule());
		// Write dates as ISO-8601 strings instead of timestamps
		mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		return mapper;
	}
}
