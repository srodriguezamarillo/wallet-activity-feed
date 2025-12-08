package com.example.wallet.activity;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Basic integration tests for the activity feed API.
 */
@SpringBootTest
@AutoConfigureMockMvc
class ActivityFeedApiTests
{

	@Autowired
	private MockMvc mockMvc;

	@Test
	void shouldReturnPagedFeedForUser() throws Exception
	{
		mockMvc.perform(get("/api/v1/activity").param("userId", "user-123")).andExpect(status().isOk())
				.andExpect(jsonPath("$.totalElements").value(6))
				// list view has no userId
				.andExpect(jsonPath("$.items[0].userId").doesNotExist())
				.andExpect(jsonPath("$.items[0].product").exists())
				.andExpect(jsonPath("$.items[0].primaryAmount.currency").exists());
	}

	@Test
	void shouldFilterByCurrencyUsd() throws Exception
	{
		mockMvc.perform(get("/api/v1/activity").param("userId", "user-123").param("currency", "USD"))
				.andExpect(status().isOk()).andExpect(jsonPath("$.totalElements").value(5))
				.andExpect(jsonPath("$.items[0].primaryAmount.currency").value("USD"));
	}

	@Test
	void shouldFilterByCurrencyUyu() throws Exception
	{
		mockMvc.perform(get("/api/v1/activity").param("userId", "user-123").param("currency", "UYU"))
				.andExpect(status().isOk()).andExpect(jsonPath("$.totalElements").value(1))
				.andExpect(jsonPath("$.items[0].primaryAmount.currency").value("UYU"))
				.andExpect(jsonPath("$.items[0].title").value("PEDIDOSYA*BURGERTIME"));
	}

	@Test
	void shouldSearchByMerchantName() throws Exception
	{
		mockMvc.perform(get("/api/v1/activity").param("userId", "user-123").param("search", "burgertime"))
				.andExpect(status().isOk()).andExpect(jsonPath("$.totalElements").value(1))
				.andExpect(jsonPath("$.items[0].title").value("PEDIDOSYA*BURGERTIME"));
	}

	@Test
	void shouldSearchByPeerName() throws Exception
	{
		mockMvc.perform(get("/api/v1/activity").param("userId", "user-123").param("search", "federico"))
				.andExpect(status().isOk()).andExpect(jsonPath("$.totalElements").value(1))
				.andExpect(jsonPath("$.items[0].product").value("P2P"))
				.andExpect(jsonPath("$.items[0].type").value("P2P_SEND"));
	}
}
