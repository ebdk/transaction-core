package com.mendel.transactioncore.application.usecases;

import com.mendel.transactioncore.domain.model.TransactionType;
import com.mendel.transactioncore.domain.ports.in.GetTransactionsIdsByTypeUseCase;
import com.mendel.transactioncore.domain.ports.out.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetTransactionsIdsByTypeUseCaseImpl implements GetTransactionsIdsByTypeUseCase {

	private final TransactionRepository repository;

	public GetTransactionsIdsByTypeUseCaseImpl(TransactionRepository repository) {
		this.repository = repository;
	}

	@Override
	public List<Long> getByType(String type) {
		var transactionType = TransactionType.fromValue(type);
		try (var stream = repository.findByType(transactionType)) {
			return stream.map(transaction -> transaction.id())
					.sorted()
					.toList();
		}
	}
}
