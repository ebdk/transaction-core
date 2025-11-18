package com.mendel.transactioncore.application.usecases;

import com.mendel.transactioncore.application.exception.TransactionAlreadyExistsException;
import com.mendel.transactioncore.application.exception.TransactionNotFoundException;
import com.mendel.transactioncore.domain.model.Transaction;
import com.mendel.transactioncore.domain.ports.in.PutTransactionCommand;
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
	public Transaction upsert(PutTransactionCommand command) {
		var transactionId = command.transactionId();
		if (repository.existsById(transactionId)) {
			throw new TransactionAlreadyExistsException(transactionId);
		}
		var parentId = command.parentId();
		if (parentId != null && !repository.existsById(parentId)) {
			throw new TransactionNotFoundException(parentId);
		}
		var transaction = new Transaction(
				transactionId,
				command.amount(),
				command.type(),
				parentId
		);
		return repository.save(transaction);
	}
}
