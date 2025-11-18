package com.mendel.transactioncore.domain.ports.out;

import com.mendel.transactioncore.domain.model.Transaction;
import com.mendel.transactioncore.domain.model.TransactionType;

import java.util.Optional;
import java.util.stream.Stream;

public interface TransactionRepository {

	Transaction save(Transaction transaction);

	Optional<Transaction> findById(long id);

	boolean existsById(long id);

	Stream<Transaction> findByType(TransactionType type);

	Stream<Transaction> findChildrenOf(long parentId);

	long nextIdentity();
}
