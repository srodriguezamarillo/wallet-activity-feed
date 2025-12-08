package com.example.wallet.activity;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.example.wallet.activity.domain.*;
import com.example.wallet.activity.repository.ActivityEventRepository;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;

/**
 * Entry point for the wallet activity feed application.
 */
@SpringBootApplication(scanBasePackages = "com.example.wallet.activity")
public class WalletActivityFeedApplication
{

	public static void main(String[] args)
	{
		SpringApplication.run(WalletActivityFeedApplication.class, args);
	}

	/**
	 * Seed a few sample activity events for local testing.
	 */
	@Bean
	CommandLineRunner seedData(ActivityEventRepository repository)
	{
		return args -> {
			if (repository.count() > 0)
			{
				return;
			}

			Instant now = Instant.now();

			ActivityEvent burgerTime = ActivityEvent.builder().userId("user-123").product(ProductType.CARD)
					.type(ActivityType.CARD_PAYMENT).amount(new BigDecimal("-2306.31")).currency("UYU")
					.status(ActivityStatus.COMPLETED).occurredAt(now.minus(3, ChronoUnit.DAYS)).createdAt(Instant.now())
					.metadata(Map.of("merchantName", "PEDIDOSYA*BURGERTIME", "cardLast4", "2421", "transactionId",
							"tx-card-001"))
					.build();

			ActivityEvent cashback = ActivityEvent.builder().userId("user-123").product(ProductType.EARNINGS)
					.type(ActivityType.CASHBACK).amount(new BigDecimal("2.86")).currency("USD")
					.status(ActivityStatus.COMPLETED).occurredAt(now.minus(1, ChronoUnit.DAYS)).createdAt(Instant.now())
					.metadata(Map.of("source", "Balance cashback")).build();

			ActivityEvent p2pSend = ActivityEvent.builder().userId("user-123").product(ProductType.P2P)
					.type(ActivityType.P2P_SEND).amount(new BigDecimal("-1.00")).currency("USD")
					.status(ActivityStatus.COMPLETED).occurredAt(now.minus(10, ChronoUnit.DAYS))
					.createdAt(Instant.now())
					.metadata(Map.of("peerName", "Federico Andres Caceres Smidt", "transferNumber", "p2p-21327b9a2684"))
					.build();

			repository.save(burgerTime);
			repository.save(cashback);
			repository.save(p2pSend);
		};
	}
}
