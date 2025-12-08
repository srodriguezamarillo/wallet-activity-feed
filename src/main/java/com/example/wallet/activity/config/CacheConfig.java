package com.example.wallet.activity.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.cache.autoconfigure.RedisCacheManagerBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager.RedisCacheManagerBuilder;

import java.time.Duration;

/**
 * Redis cache configuration for the activity feed.
 */
@Configuration
@ConditionalOnProperty(name = "spring.cache.type", havingValue = "redis")
public class CacheConfig
{

	@Bean
	public RedisCacheManagerBuilderCustomizer
			redisCacheManagerBuilderCustomizer(@Value("${app.cache.activity-feed-ttl-seconds:60}") long ttlSeconds)
	{

		return (RedisCacheManagerBuilder builder) -> builder.withCacheConfiguration("activityFeed",
				RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofSeconds(ttlSeconds)));
	}
}
