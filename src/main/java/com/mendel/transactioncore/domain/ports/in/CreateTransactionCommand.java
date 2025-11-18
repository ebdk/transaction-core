package com.mendel.transactioncore.domain.ports.in;

import com.mendel.transactioncore.domain.model.TransactionType;

import java.math.BigDecimal;
import java.util.Objects;

public record CreateTransactionCommand(BigDecimal amount, TransactionType type, Long parentId) {

	public CreateTransactionCommand {
		Objects.requireNonNull(amount, "amount is required");
		Objects.requireNonNull(type, "type is required");
	}
}
