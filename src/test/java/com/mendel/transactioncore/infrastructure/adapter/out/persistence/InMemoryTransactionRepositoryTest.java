package com.mendel.transactioncore.infrastructure.adapter.out.persistence;

import com.mendel.transactioncore.domain.model.Transaction;
import com.mendel.transactioncore.domain.model.TransactionType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryTransactionRepositoryTest {

	private final InMemoryTransactionRepository repository = new InMemoryTransactionRepository();

	@AfterEach
	void tearDown() {
		repository.clear();
	}

	@Test
	void savesTransactionAndMakesItDiscoverable() {
		var id = repository.nextIdentity();
		var transaction = new Transaction(id, new BigDecimal("123.45"), TransactionType.DEPOSIT, null);

		repository.save(transaction);

		assertTrue(repository.existsById(id));
	}

	@Test
	void nextIdentityIncrementsSequentially() {
		var first = repository.nextIdentity();
		var second = repository.nextIdentity();

		assertEquals(first + 1, second);
	}
}
