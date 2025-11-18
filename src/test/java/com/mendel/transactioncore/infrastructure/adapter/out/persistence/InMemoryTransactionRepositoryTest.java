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

	@Test
	void findByIdReturnsSavedTransaction() {
		var transaction = new Transaction(42L, new BigDecimal("10.00"), TransactionType.DEPOSIT, null);
		repository.save(transaction);

		var result = repository.findById(42L);

		assertTrue(result.isPresent());
		assertEquals(transaction, result.get());
	}

	@Test
	void findChildrenOfReturnsOnlyMatches() {
		repository.save(new Transaction(1L, new BigDecimal("10.00"), TransactionType.DEPOSIT, null));
		repository.save(new Transaction(2L, new BigDecimal("5.00"), TransactionType.DEPOSIT, 1L));
		repository.save(new Transaction(3L, new BigDecimal("7.00"), TransactionType.WITHDRAWAL, 2L));

		var children = repository.findChildrenOf(1L).toList();

		assertEquals(1, children.size());
		assertEquals(2L, children.get(0).id());
	}
}
