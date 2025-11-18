package com.mendel.transactioncore.infrastructure.adapter.in.web;

import com.mendel.transactioncore.application.exception.TransactionNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;

@RestControllerAdvice
class ApiExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

	@ExceptionHandler(TransactionNotFoundException.class)
	ProblemDetail handleTransactionNotFound(TransactionNotFoundException exception) {
		log.warn("Transaction not found", exception);
		return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	ProblemDetail handleValidation(MethodArgumentNotValidException exception) {
		log.warn("Validation error", exception);
		var detail = exception.getBindingResult().getFieldErrors().stream()
				.findFirst()
				.map(error -> "%s %s".formatted(error.getField(), Objects.requireNonNullElse(error.getDefaultMessage(), "is invalid")))
				.orElse("Invalid request");
		return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, detail);
	}

	@ExceptionHandler(IllegalArgumentException.class)
	ProblemDetail handleIllegalArgument(IllegalArgumentException exception) {
		log.warn("Illegal argument", exception);
		return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage());
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	ProblemDetail handleUnreadable(HttpMessageNotReadableException exception) {
		log.warn("Malformed request", exception);
		var cause = exception.getMostSpecificCause();
		if (cause instanceof IllegalArgumentException iae) {
			return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, iae.getMessage());
		}
		return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Malformed JSON request");
	}

	@ExceptionHandler(HttpMediaTypeNotSupportedException.class)
	ProblemDetail handleMediaType(HttpMediaTypeNotSupportedException exception) {
		log.warn("Unsupported media type", exception);
		return ProblemDetail.forStatusAndDetail(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Unsupported content type");
	}
}
