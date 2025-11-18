package com.mendel.transactioncore.domain.ports.in;

import com.mendel.transactioncore.domain.model.Transaction;

public interface PutTransactionUseCase {

	Transaction upsert(PutTransactionInput command);
}
