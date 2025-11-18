package com.mendel.transactioncore.infrastructure.adapter.in.web;

import com.mendel.transactioncore.application.exception.TransactionNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;

@RestControllerAdvice
class ApiExceptionHandler {

	@ExceptionHandler(TransactionNotFoundException.class)
	ProblemDetail handleTransactionNotFound(TransactionNotFoundException exception) {
		return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	ProblemDetail handleValidation(MethodArgumentNotValidException exception) {
		var detail = exception.getBindingResult().getFieldErrors().stream()
				.findFirst()
				.map(error -> "%s %s".formatted(error.getField(), Objects.requireNonNullElse(error.getDefaultMessage(), "is invalid")))
				.orElse("Invalid request");
		return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, detail);
	}
}
