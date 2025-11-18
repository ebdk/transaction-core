package com.mendel.transactioncore.infrastructure.adapter.in.web;

import com.mendel.transactioncore.domain.ports.in.GetTransactionSumUseCase;
import com.mendel.transactioncore.domain.ports.in.GetTransactionsIdsByTypeUseCase;
import com.mendel.transactioncore.domain.ports.in.PutTransactionInput;
import com.mendel.transactioncore.domain.ports.in.PutTransactionUseCase;
import com.mendel.transactioncore.infrastructure.adapter.in.web.dto.TransactionRequest;
import com.mendel.transactioncore.infrastructure.adapter.in.web.dto.TransactionStatusResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

	private final PutTransactionUseCase putTransactionUseCase;
	private final GetTransactionsIdsByTypeUseCase getTransactionsIdsByTypeUseCase;
	private final GetTransactionSumUseCase getTransactionSumUseCase;

	public TransactionController(PutTransactionUseCase putTransactionUseCase,
			GetTransactionsIdsByTypeUseCase getTransactionsIdsByTypeUseCase,
			GetTransactionSumUseCase getTransactionSumUseCase) {
		this.putTransactionUseCase = putTransactionUseCase;
		this.getTransactionsIdsByTypeUseCase = getTransactionsIdsByTypeUseCase;
		this.getTransactionSumUseCase = getTransactionSumUseCase;
	}

	@PutMapping("/{transactionId}")
	public ResponseEntity<TransactionStatusResponse> put(@PathVariable long transactionId,
														 @Valid @RequestBody TransactionRequest request) {
		var command = new PutTransactionInput(transactionId, request.amount(), request.type(), request.parentId());
		putTransactionUseCase.upsert(command);
		return ResponseEntity.ok(new TransactionStatusResponse("ok"));
	}

	@GetMapping("/types/{type}")
	public ResponseEntity<List<Long>> getIdsByType(@PathVariable String type) {
		var ids = getTransactionsIdsByTypeUseCase.getByType(type);
		return ResponseEntity.ok(ids);
	}

	@GetMapping("/sum/{transactionId}")
	public ResponseEntity<Map<String, Number>> getSum(@PathVariable long transactionId) {
		var sum = getTransactionSumUseCase.getSum(transactionId);
		return ResponseEntity.ok(Map.of("sum", sum));
	}
}
