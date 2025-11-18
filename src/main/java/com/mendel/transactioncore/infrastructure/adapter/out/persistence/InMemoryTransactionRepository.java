package com.mendel.transactioncore.infrastructure.adapter.out.persistence;

import com.mendel.transactioncore.domain.model.Transaction;
import com.mendel.transactioncore.domain.model.TransactionType;
import com.mendel.transactioncore.domain.ports.out.TransactionRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

@Repository
public class InMemoryTransactionRepository implements TransactionRepository {

	private final ConcurrentMap<Long, Transaction> storage = new ConcurrentHashMap<>();
	private final AtomicLong sequence = new AtomicLong();

	@Override
	public Transaction save(Transaction transaction) {
		storage.put(transaction.id(), transaction);
		return transaction;
	}

	@Override
	public Optional<Transaction> findById(long id) {
		return Optional.ofNullable(storage.get(id));
	}

	@Override
	public boolean existsById(long id) {
		return storage.containsKey(id);
	}

	@Override
	public Stream<Transaction> findByType(TransactionType type) {
		return storage.values().stream()
				.filter(tx -> tx.type() == type);
	}

	@Override
	public Stream<Transaction> findChildrenOf(long parentId) {
		return storage.values().stream()
				.filter(tx -> tx.parentId() != null && tx.parentId() == parentId);
	}

	@Override
	public long nextIdentity() {
		return sequence.incrementAndGet();
	}

	public void clear() {
		storage.clear();
		sequence.set(0);
	}
}
