package com.mendel.transactioncore.application.usecases;

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
import static org.junit.jupiter.api.Assertions.assertTrue;

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
	@SuppressWarnings("DataFlowIssue")
	void failsWhenParentDoesNotExist() {
		var command = new PutTransactionInput(8L, new BigDecimal("5.00"), TransactionType.WITHDRAWAL, 1L);

		assertThrows(TransactionNotFoundException.class, () -> useCase.upsert(command));
	}

	@Test
	void updatesExistingTransactionWhenIdAlreadyExists() {
		var existing = new Transaction(7L, new BigDecimal("1.00"), TransactionType.DEPOSIT, null);
		repository.save(existing);
		var command = new PutTransactionInput(7L, new BigDecimal("5.00"), TransactionType.WITHDRAWAL, null);

		var updated = useCase.upsert(command);

		assertEquals(new BigDecimal("5.00"), updated.amount());
		assertEquals(TransactionType.WITHDRAWAL, updated.type());
		assertTrue(repository.findById(7L).isPresent());
	}

	@Test
	void failsWhenParentReferencesItself() {
		var command = new PutTransactionInput(9L, new BigDecimal("5.00"), TransactionType.DEPOSIT, 9L);

		assertThrows(IllegalArgumentException.class, () -> useCase.upsert(command));
	}

	@Test
	void failsWhenParentIsNegative() {
		var command = new PutTransactionInput(9L, new BigDecimal("5.00"), TransactionType.DEPOSIT, -1L);

		assertThrows(IllegalArgumentException.class, () -> useCase.upsert(command));
	}

	@Test
	void failsWhenParentCreatesCycle() {
		repository.save(new Transaction(1L, new BigDecimal("1.00"), TransactionType.DEPOSIT, null));
		repository.save(new Transaction(2L, new BigDecimal("1.00"), TransactionType.DEPOSIT, 1L));
		var command = new PutTransactionInput(1L, new BigDecimal("2.00"), TransactionType.DEPOSIT, 2L);

		assertThrows(IllegalArgumentException.class, () -> useCase.upsert(command));
	}
}
