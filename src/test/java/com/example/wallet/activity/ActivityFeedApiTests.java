package com.example.wallet.activity;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for the activity feed API endpoints.
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
				.andExpect(jsonPath("$.totalElements").value(6)).andExpect(jsonPath("$.page").value(0))
				.andExpect(jsonPath("$.size").value(20)).andExpect(jsonPath("$.items[0].userId").doesNotExist())
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
	void shouldFilterByProduct() throws Exception
	{
		mockMvc.perform(get("/api/v1/activity").param("userId", "user-123").param("product", "CARD"))
				.andExpect(status().isOk()).andExpect(jsonPath("$.items[0].product").value("CARD"));
	}

	@Test
	void shouldFilterByStatus() throws Exception
	{
		mockMvc.perform(get("/api/v1/activity").param("userId", "user-123").param("status", "COMPLETED"))
				.andExpect(status().isOk()).andExpect(jsonPath("$.items[0].status").value("COMPLETED"));
	}

	@Test
	void shouldFilterByCombinedFilters() throws Exception
	{
		mockMvc.perform(get("/api/v1/activity").param("userId", "user-123").param("product", "CARD")
				.param("status", "COMPLETED").param("currency", "USD")).andExpect(status().isOk())
				.andExpect(jsonPath("$.items[0].product").value("CARD"))
				.andExpect(jsonPath("$.items[0].status").value("COMPLETED"))
				.andExpect(jsonPath("$.items[0].primaryAmount.currency").value("USD"));
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

	@Test
	void shouldSupportPagination() throws Exception
	{
		mockMvc.perform(get("/api/v1/activity").param("userId", "user-123").param("page", "0").param("size", "2"))
				.andExpect(status().isOk()).andExpect(jsonPath("$.size").value(2))
				.andExpect(jsonPath("$.items.length()").value(2)).andExpect(jsonPath("$.hasNext").value(true));
	}

	@Test
	void shouldReturnEmptyResultForNonExistentUser() throws Exception
	{
		mockMvc.perform(get("/api/v1/activity").param("userId", "non-existent-user")).andExpect(status().isOk())
				.andExpect(jsonPath("$.totalElements").value(0)).andExpect(jsonPath("$.items.length()").value(0));
	}

	@Test
	void shouldReturnActivityDetail() throws Exception
	{
		// First get an activity ID from the feed
		String response = mockMvc.perform(get("/api/v1/activity").param("userId", "user-123"))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

		// Extract first ID (simplified - in real scenario use JSON parsing)
		// For now, we'll test with a known pattern
		mockMvc.perform(get("/api/v1/activity/97986d3d-2933-4664-afd8-7637ae1de726")).andExpect(status().isOk())
				.andExpect(jsonPath("$.id").exists()).andExpect(jsonPath("$.userId").value("user-123"))
				.andExpect(jsonPath("$.metadata").exists());
	}

	@Test
	void shouldReturnNotFoundForInvalidActivityId() throws Exception
	{
		mockMvc.perform(get("/api/v1/activity/invalid-id")).andExpect(status().isBadRequest());
	}

	@Test
	void shouldRequireUserId() throws Exception
	{
		mockMvc.perform(get("/api/v1/activity")).andExpect(status().isBadRequest());
	}
}
