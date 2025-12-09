package com.example.wallet.activity.domain;

/**
 * Exception thrown when an activity event is not found.
 */
public class ActivityNotFoundException extends RuntimeException
{

	public ActivityNotFoundException(String message)
	{
		super(message);
	}

	public ActivityNotFoundException(String message, Throwable cause)
	{
		super(message, cause);
	}
}

