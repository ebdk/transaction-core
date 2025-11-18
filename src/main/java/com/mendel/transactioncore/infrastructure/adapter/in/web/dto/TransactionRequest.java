package com.mendel.transactioncore.infrastructure.adapter.in.web.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.mendel.transactioncore.domain.model.TransactionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record TransactionRequest(
		@NotNull @DecimalMin(value = "0.0", inclusive = false) BigDecimal amount,
		@NotNull TransactionType type,
		@JsonAlias("parent_id") Long parentId
) {
}
