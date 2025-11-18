package com.mendel.transactioncore.domain.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Locale;

public enum TransactionType {
	DEPOSIT,
	WITHDRAWAL;

	@JsonCreator
	public static TransactionType fromValue(String value) {
		if (value == null || value.isBlank()) {
			throw new IllegalArgumentException("Transaction type must be provided");
		}
		return TransactionType.valueOf(value.trim().toUpperCase(Locale.ROOT));
	}

	@JsonValue
	public String jsonValue() {
		return name().toLowerCase(Locale.ROOT);
	}
}
