package com.mendel.transactioncore.application.usecases;

import com.mendel.transactioncore.application.exception.TransactionAlreadyExistsException;
import com.mendel.transactioncore.application.exception.TransactionNotFoundException;
import com.mendel.transactioncore.domain.model.Transaction;
import com.mendel.transactioncore.domain.model.TransactionType;
import com.mendel.transactioncore.domain.ports.in.PutTransactionInput;
import com.mendel.transactioncore.infrastructure.adapter.out.persistence.InMemoryTransactionRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PutTransactionUseCaseImplTest {

	private final InMemoryTransactionRepository repository = new InMemoryTransactionRepository();
	private final PutTransactionUseCaseImpl useCase = new PutTransactionUseCaseImpl(repository);

	@AfterEach
	void tearDown() {
		repository.clear();
	}

	@Test
	void storesTransactionWithProvidedId() {
		var command = new PutTransactionInput(10L, new BigDecimal("1000.00"), TransactionType.DEPOSIT, null);

		var transaction = useCase.upsert(command);

		assertEquals(10L, transaction.id());
		assertEquals(new BigDecimal("1000.00"), transaction.amount());
		assertEquals(TransactionType.DEPOSIT, transaction.type());
	}

	@Test
	void failsWhenTransactionAlreadyExists() {
		var existing = new Transaction(7L, new BigDecimal("1.00"), TransactionType.DEPOSIT, null);
		repository.save(existing);
		var command = new PutTransactionInput(7L, new BigDecimal("5.00"), TransactionType.WITHDRAWAL, null);

		assertThrows(TransactionAlreadyExistsException.class, () -> useCase.upsert(command));
	}

	@Test
	@SuppressWarnings("DataFlowIssue")
	void failsWhenParentDoesNotExist() {
		var command = new PutTransactionInput(8L, new BigDecimal("5.00"), TransactionType.WITHDRAWAL, 1L);

		assertThrows(TransactionNotFoundException.class, () -> useCase.upsert(command));
	}
}
