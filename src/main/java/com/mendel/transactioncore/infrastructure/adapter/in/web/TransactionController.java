package com.mendel.transactioncore.infrastructure.adapter.in.web;

import com.mendel.transactioncore.domain.ports.in.CreateTransactionInput;
import com.mendel.transactioncore.domain.ports.in.CreateTransactionUseCase;
import com.mendel.transactioncore.domain.ports.in.GetTransactionsIdsByTypeUseCase;
import com.mendel.transactioncore.domain.ports.in.PutTransactionInput;
import com.mendel.transactioncore.domain.ports.in.PutTransactionUseCase;
import com.mendel.transactioncore.infrastructure.adapter.in.web.dto.CreateTransactionRequest;
import com.mendel.transactioncore.infrastructure.adapter.in.web.dto.TransactionResponse;
import com.mendel.transactioncore.infrastructure.adapter.in.web.dto.TransactionStatusResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

	private final CreateTransactionUseCase createTransactionUseCase;
	private final PutTransactionUseCase putTransactionUseCase;
	private final GetTransactionsIdsByTypeUseCase getTransactionsIdsByTypeUseCase;

	public TransactionController(CreateTransactionUseCase createTransactionUseCase,
			PutTransactionUseCase putTransactionUseCase,
			GetTransactionsIdsByTypeUseCase getTransactionsIdsByTypeUseCase) {
		this.createTransactionUseCase = createTransactionUseCase;
		this.putTransactionUseCase = putTransactionUseCase;
		this.getTransactionsIdsByTypeUseCase = getTransactionsIdsByTypeUseCase;
	}

	@PostMapping
	public ResponseEntity<TransactionResponse> create(@Valid @RequestBody CreateTransactionRequest request) {
		var command = new CreateTransactionInput(request.amount(), request.type(), request.parentId());
		var transaction = createTransactionUseCase.create(command);
		var response = new TransactionResponse(transaction.id(), transaction.amount(), transaction.type(), transaction.parentId());
		return ResponseEntity.created(URI.create("/transactions/%d".formatted(transaction.id()))).body(response);
	}

	@PutMapping("/{transactionId}")
	public ResponseEntity<TransactionStatusResponse> put(@PathVariable long transactionId,
														 @Valid @RequestBody CreateTransactionRequest request) {
		var command = new PutTransactionInput(transactionId, request.amount(), request.type(), request.parentId());
		putTransactionUseCase.upsert(command);
		return ResponseEntity.ok(new TransactionStatusResponse("ok"));
	}

	@GetMapping("/types/{type}")
	public ResponseEntity<List<Long>> getIdsByType(@PathVariable String type) {
		var ids = getTransactionsIdsByTypeUseCase.getByType(type);
		return ResponseEntity.ok(ids);
	}
}
