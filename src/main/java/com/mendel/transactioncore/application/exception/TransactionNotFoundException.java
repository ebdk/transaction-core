package com.mendel.transactioncore.application.exception;

public class TransactionNotFoundException extends RuntimeException {

	public TransactionNotFoundException(long id) {
		super("Transaction %d not found".formatted(id));
	}
}
