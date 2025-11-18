package com.mendel.transactioncore.domain.ports.in;

import com.mendel.transactioncore.domain.model.Transaction;

public interface CreateTransactionUseCase {

	Transaction create(CreateTransactionCommand command);
}
