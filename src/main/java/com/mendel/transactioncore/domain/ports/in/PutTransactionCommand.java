package com.mendel.transactioncore.domain.ports.in;

import com.mendel.transactioncore.domain.model.TransactionType;

import java.math.BigDecimal;
import java.util.Objects;

public record PutTransactionCommand(long transactionId, BigDecimal amount, TransactionType type, Long parentId) {

	public PutTransactionCommand {
		Objects.requireNonNull(amount, "amount is required");
		Objects.requireNonNull(type, "type is required");
		if (transactionId <= 0) {
			throw new IllegalArgumentException("transactionId must be positive");
		}
	}
}
