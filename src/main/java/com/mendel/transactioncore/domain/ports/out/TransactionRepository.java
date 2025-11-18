package com.mendel.transactioncore.domain.ports.out;

import com.mendel.transactioncore.domain.model.Transaction;

public interface TransactionRepository {

	Transaction save(Transaction transaction);

	boolean existsById(long id);

	long nextIdentity();
}
