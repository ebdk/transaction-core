package com.mendel.transactioncore.domain.ports.in;

import com.mendel.transactioncore.domain.model.TransactionType;

import java.math.BigDecimal;
import java.util.Objects;

public record CreateTransactionInput(BigDecimal amount, TransactionType type, Long parentId) {

	public CreateTransactionInput {
		Objects.requireNonNull(amount, "amount is required");
		Objects.requireNonNull(type, "type is required");
	}
}
