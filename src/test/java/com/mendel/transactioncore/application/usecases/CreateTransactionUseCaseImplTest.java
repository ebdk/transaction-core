package com.mendel.transactioncore.application.usecases;

import com.mendel.transactioncore.application.exception.TransactionNotFoundException;
import com.mendel.transactioncore.domain.model.Transaction;
import com.mendel.transactioncore.domain.model.TransactionType;
import com.mendel.transactioncore.domain.ports.in.CreateTransactionInput;
import com.mendel.transactioncore.infrastructure.adapter.out.persistence.InMemoryTransactionRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CreateTransactionUseCaseImplTest {

	private final InMemoryTransactionRepository repository = new InMemoryTransactionRepository();
	private final CreateTransactionUseCaseImpl useCase = new CreateTransactionUseCaseImpl(repository);

	@AfterEach
	void tearDown() {
		repository.clear();
	}

	@Test
	void createsTransactionWithoutParent() {
		var command = new CreateTransactionInput(new BigDecimal("1000.00"), TransactionType.DEPOSIT, null);

		var transaction = useCase.create(command);

		assertEquals(1L, transaction.id());
		assertEquals(new BigDecimal("1000.00"), transaction.amount());
		assertEquals(TransactionType.DEPOSIT, transaction.type());
	}

	@Test
	void createsTransactionWithExistingParent() {
		var parentId = repository.nextIdentity();
		var parent = new Transaction(parentId, new BigDecimal("10.00"), TransactionType.DEPOSIT, null);
		repository.save(parent);
		var command = new CreateTransactionInput(new BigDecimal("20.00"), TransactionType.WITHDRAWAL, parentId);

		var transaction = useCase.create(command);

		assertEquals(parentId + 1, transaction.id());
		assertEquals(parentId, transaction.parentId());
	}

	@Test
	void failsWhenParentDoesNotExist() {
		var command = new CreateTransactionInput(new BigDecimal("10.00"), TransactionType.DEPOSIT, 999L);

		assertThrows(TransactionNotFoundException.class, () -> useCase.create(command));
	}
}
