package com.mendel.transactioncore.application.usecases;

import com.mendel.transactioncore.application.exception.TransactionNotFoundException;
import com.mendel.transactioncore.domain.model.Transaction;
import com.mendel.transactioncore.domain.ports.in.PutTransactionInput;
import com.mendel.transactioncore.domain.ports.in.PutTransactionUseCase;
import com.mendel.transactioncore.domain.ports.out.TransactionRepository;
import org.springframework.stereotype.Service;

@Service
public class PutTransactionUseCaseImpl implements PutTransactionUseCase {

	private final TransactionRepository repository;

	public PutTransactionUseCaseImpl(TransactionRepository repository) {
		this.repository = repository;
	}

	@Override
	public Transaction upsert(PutTransactionInput command) {
		var transactionId = command.transactionId();
		var parentId = command.parentId();
		if (parentId != null) {
			if (parentId < 0) {
				throw new IllegalArgumentException("parentId cannot be negative");
			}
			if (parentId == transactionId) {
				throw new IllegalArgumentException("Transaction cannot reference itself as parent");
			}
			if (!repository.existsById(parentId)) {
				throw new TransactionNotFoundException(parentId);
			}
			validateNoCycles(transactionId, parentId);
		}
		var transaction = new Transaction(
				transactionId,
				command.amount(),
				command.type(),
				parentId
		);
		return repository.save(transaction);
	}

	private void validateNoCycles(long transactionId, long parentId) {
		var current = parentId;
		while (true) {
			if (current == transactionId) {
				throw new IllegalArgumentException("Circular parent reference detected");
			}
			var next = repository.findById(current)
					.map(Transaction::parentId)
					.orElse(null);
			if (next == null) {
				return;
			}
			current = next;
		}
	}
}
