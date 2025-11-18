package com.mendel.transactioncore.application.usecases;

import com.mendel.transactioncore.domain.model.Transaction;
import com.mendel.transactioncore.domain.model.TransactionType;
import com.mendel.transactioncore.infrastructure.adapter.out.persistence.InMemoryTransactionRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GetTransactionsIdsByTypeUseCaseImplTest {

	private final InMemoryTransactionRepository repository = new InMemoryTransactionRepository();
	private final GetTransactionsIdsByTypeUseCaseImpl useCase = new GetTransactionsIdsByTypeUseCaseImpl(repository);

	@AfterEach
	void tearDown() {
		repository.clear();
	}

	@Test
	void returnsIdsMatchingTypeIgnoringCase() {
		repository.save(new Transaction(1L, new BigDecimal("10.00"), TransactionType.DEPOSIT, null));
		repository.save(new Transaction(2L, new BigDecimal("15.00"), TransactionType.WITHDRAWAL, null));
		repository.save(new Transaction(3L, new BigDecimal("20.00"), TransactionType.DEPOSIT, null));

		var ids = useCase.getByType("deposit");

		assertEquals(2, ids.size());
		assertEquals(1L, ids.get(0));
		assertEquals(3L, ids.get(1));
	}
}
