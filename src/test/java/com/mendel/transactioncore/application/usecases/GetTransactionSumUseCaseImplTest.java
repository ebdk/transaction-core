package com.mendel.transactioncore.application.usecases;

import com.mendel.transactioncore.application.exception.TransactionNotFoundException;
import com.mendel.transactioncore.domain.model.Transaction;
import com.mendel.transactioncore.domain.model.TransactionType;
import com.mendel.transactioncore.infrastructure.adapter.out.persistence.InMemoryTransactionRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GetTransactionSumUseCaseImplTest {

	private final InMemoryTransactionRepository repository = new InMemoryTransactionRepository();
	private final GetTransactionSumUseCaseImpl useCase = new GetTransactionSumUseCaseImpl(repository);

	@AfterEach
	void tearDown() {
		repository.clear();
	}

	@Test
	void computesSumAcrossParentChain() {
		repository.save(new Transaction(10L, new BigDecimal("1000"), TransactionType.DEPOSIT, null));
		repository.save(new Transaction(11L, new BigDecimal("200"), TransactionType.DEPOSIT, 10L));
		repository.save(new Transaction(12L, new BigDecimal("300"), TransactionType.WITHDRAWAL, 11L));

		var sum = useCase.getSum(10L);

		assertEquals(new BigDecimal("1500"), sum);
	}

	@Test
	void throwsWhenTransactionMissing() {
		assertThrows(TransactionNotFoundException.class, () -> useCase.getSum(99L));
	}
}
