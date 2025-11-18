package com.mendel.transactioncore.domain.model;

import java.math.BigDecimal;
import java.util.Objects;

public record Transaction(long id, BigDecimal amount, TransactionType type, Long parentId) {

	public Transaction {
		Objects.requireNonNull(amount, "amount is required");
		Objects.requireNonNull(type, "type is required");
		if (amount.signum() < 0) {
			throw new IllegalArgumentException("amount cannot be negative");
		}
	}
}
