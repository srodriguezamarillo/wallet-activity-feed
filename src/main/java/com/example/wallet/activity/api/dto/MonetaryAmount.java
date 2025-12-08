package com.example.wallet.activity.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Monetary amount represented in a single currency.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonetaryAmount
{

	private String currency;
	private BigDecimal value;
}
