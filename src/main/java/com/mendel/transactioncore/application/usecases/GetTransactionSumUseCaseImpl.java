package com.mendel.transactioncore.application.usecases;

import com.mendel.transactioncore.application.exception.TransactionNotFoundException;
import com.mendel.transactioncore.domain.model.Transaction;
import com.mendel.transactioncore.domain.ports.in.GetTransactionSumUseCase;
import com.mendel.transactioncore.domain.ports.out.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class GetTransactionSumUseCaseImpl implements GetTransactionSumUseCase {

	private final TransactionRepository repository;

	public GetTransactionSumUseCaseImpl(TransactionRepository repository) {
		this.repository = repository;
	}

	@Override
	public BigDecimal getSum(long transactionId) {
		var root = repository.findById(transactionId)
				.orElseThrow(() -> new TransactionNotFoundException(transactionId));
		return sumRecursive(root);
	}

	private BigDecimal sumRecursive(Transaction transaction) {
		var childrenSum = repository.findChildrenOf(transaction.id())
				.map(this::sumRecursive)
				.reduce(BigDecimal.ZERO, BigDecimal::add);
		return transaction.amount().add(childrenSum);
	}
}
