package com.example.wallet.activity;

import com.example.wallet.activity.api.ErrorResponse;
import com.example.wallet.activity.domain.ActivityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Global exception handler for REST API errors. Provides structured error
 * responses and comprehensive logging.
 */
@RestControllerAdvice
@Slf4j
public class GlobalApiExceptionHandler
{

	@ExceptionHandler(ActivityNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleActivityNotFound(ActivityNotFoundException ex,
			HttpServletRequest request)
	{
		log.warn("Activity not found: {} - Path: {}", ex.getMessage(), request.getRequestURI());

		ErrorResponse error = ErrorResponse.builder().error("Activity not found").message(ex.getMessage())
				.timestamp(Instant.now()).path(request.getRequestURI()).build();

		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request)
	{
		log.warn("Invalid argument: {} - Path: {}", ex.getMessage(), request.getRequestURI());

		ErrorResponse error = ErrorResponse.builder().error("Invalid argument").message(ex.getMessage())
				.timestamp(Instant.now()).path(request.getRequestURI()).build();

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex,
			HttpServletRequest request)
	{
		Map<String, Object> validationErrors = ex.getBindingResult().getFieldErrors().stream()
				.collect(Collectors.toMap(error -> error.getField(),
						error -> error.getDefaultMessage() != null ? error.getDefaultMessage() : "Invalid value",
						(existing, replacement) -> existing));

		log.warn("Validation failed: {} - Path: {}", validationErrors, request.getRequestURI());

		ErrorResponse error =
				ErrorResponse.builder().error("Validation failed").message("One or more validation errors occurred")
						.timestamp(Instant.now()).path(request.getRequestURI()).details(validationErrors).build();

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, HttpServletRequest request)
	{
		log.error("Unexpected error processing request: {} - Path: {}", request.getRequestURI(), ex);

		ErrorResponse error = ErrorResponse.builder().error("Internal server error")
				.message("An unexpected error occurred").timestamp(Instant.now()).path(request.getRequestURI()).build();

		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
	}

	@ExceptionHandler(MissingServletRequestParameterException.class)
	public ResponseEntity<ErrorResponse> handleMissingRequestParam(MissingServletRequestParameterException ex,
			HttpServletRequest request)
	{
		Map<String, Object> details = Map.of("parameter", ex.getParameterName());

		log.warn("Missing request parameter: {} - Path: {}", ex.getParameterName(), request.getRequestURI());

		ErrorResponse error = ErrorResponse.builder().error("Missing request parameter").message(ex.getMessage())
				.timestamp(Instant.now()).path(request.getRequestURI()).details(details).build();

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	}
}
