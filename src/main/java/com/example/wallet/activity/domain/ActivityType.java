package com.example.wallet.activity.domain;

/**
 * Specific business action represented by the event.
 */
public enum ActivityType
{
	CARD_PAYMENT,
	CARD_REFUND,
	P2P_SEND,
	P2P_RECEIVE,
	CRYPTO_BUY,
	CRYPTO_SELL,
	CASHBACK,
	WALLET_TOPUP,
	BILL_PAYMENT
}
