package com.mendel.transactioncore.application.exception;

public class TransactionAlreadyExistsException extends RuntimeException {

	public TransactionAlreadyExistsException(long id) {
		super("Transaction %d already exists".formatted(id));
	}
}
