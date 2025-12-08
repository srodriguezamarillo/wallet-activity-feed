package com.example.wallet.activity.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * JPA attribute converter to store metadata as JSON text.
 */
@Converter
public class MetadataConverter implements AttributeConverter<Map<String, Object>, String>
{

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	@Override
	public String convertToDatabaseColumn(Map<String, Object> attribute)
	{
		if (attribute == null || attribute.isEmpty())
		{
			return "{}";
		}
		try
		{
			return OBJECT_MAPPER.writeValueAsString(attribute);
		}
		catch (JsonProcessingException ex)
		{
			throw new IllegalStateException("Failed to serialize metadata", ex);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public Map<String, Object> convertToEntityAttribute(String dbData)
	{
		if (dbData == null || dbData.isBlank())
		{
			return new HashMap<>();
		}
		try
		{
			return OBJECT_MAPPER.readValue(dbData, HashMap.class);
		}
		catch (IOException ex)
		{
			throw new IllegalStateException("Failed to deserialize metadata", ex);
		}
	}
}
