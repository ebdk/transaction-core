package com.mendel.transactioncore.application.usecases;

import com.mendel.transactioncore.application.exception.TransactionNotFoundException;
import com.mendel.transactioncore.domain.model.Transaction;
import com.mendel.transactioncore.domain.ports.in.CreateTransactionInput;
import com.mendel.transactioncore.domain.ports.in.CreateTransactionUseCase;
import com.mendel.transactioncore.domain.ports.out.TransactionRepository;
import org.springframework.stereotype.Service;

@Service
public class CreateTransactionUseCaseImpl implements CreateTransactionUseCase {

	private final TransactionRepository repository;

	public CreateTransactionUseCaseImpl(TransactionRepository repository) {
		this.repository = repository;
	}

	@Override
	public Transaction create(CreateTransactionInput command) {
		var parentId = command.parentId();
		if (parentId != null && !repository.existsById(parentId)) {
			throw new TransactionNotFoundException(parentId);
		}
		var transaction = new Transaction(
				repository.nextIdentity(),
				command.amount(),
				command.type(),
				parentId
		);
		return repository.save(transaction);
	}
}
