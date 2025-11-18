package com.mendel.transactioncore.infrastructure.adapter.in.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.mendel.transactioncore.domain.model.TransactionType;

import java.math.BigDecimal;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record TransactionResponse(long id, BigDecimal amount, TransactionType type, Long parentId) {
}
