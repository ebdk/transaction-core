package com.mendel.transactioncore.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class TransactionControllerIT {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void storesTransactionWithPut() throws Exception {
		var request = json("/contracts/put-transaction/put-transaction-success-request.json");
		var response = json("/contracts/put-transaction/put-transaction-success-response.json");

		mockMvc.perform(put("/transactions/{id}", 10)
						.contentType(MediaType.APPLICATION_JSON)
						.content(request))
				.andExpect(status().isOk())
				.andExpect(content().json(response));
	}

	@Test
	void putSupportsParentRelationship() throws Exception {
		var parentRequest = json("/contracts/put-transaction/put-transaction-success-request.json");
		mockMvc.perform(put("/transactions/{id}", 10)
						.contentType(MediaType.APPLICATION_JSON)
						.content(parentRequest))
				.andExpect(status().isOk());

		var childRequest = json("/contracts/put-transaction/put-transaction-child-request.json");

		mockMvc.perform(put("/transactions/{id}", 11)
						.contentType(MediaType.APPLICATION_JSON)
						.content(childRequest))
				.andExpect(status().isOk());
	}

	@Test
	void putOverwritesExistingTransaction() throws Exception {
		putTransaction(10, "/contracts/put-transaction/put-transaction-success-request.json");
		var update = json("/contracts/put-transaction/put-transaction-update-request.json");

		mockMvc.perform(put("/transactions/{id}", 10)
						.contentType(MediaType.APPLICATION_JSON)
						.content(update))
				.andExpect(status().isOk());

		mockMvc.perform(get("/transactions/sum/{id}", 10))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.sum").value(7500));
	}

	@Test
	void putFailsWhenParentMissing() throws Exception {
		var request = json("/contracts/put-transaction/put-transaction-missing-parent-request.json");

		mockMvc.perform(put("/transactions/{id}", 21)
						.contentType(MediaType.APPLICATION_JSON)
						.content(request))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.detail").value("Transaction 999 not found"));
	}

	@Test
	void returnsIdsByType() throws Exception {
		putTransaction(10, "/contracts/put-transaction/put-transaction-success-request.json");
		putTransaction(12, "/contracts/put-transaction/put-transaction-success-request.json");
		var expected = json("/contracts/get-transactions-by-type/get-by-type-deposit-response.json");

		mockMvc.perform(get("/transactions/types/{type}", "deposit"))
				.andExpect(status().isOk())
				.andExpect(content().json(expected));
	}

	@Test
	void rejectsUnknownType() throws Exception {
		mockMvc.perform(get("/transactions/types/{type}", "planes"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.detail").value("Unsupported transaction type: planes"));
	}

	@Test
	void returnsSumForTransactionChain() throws Exception {
		putTransaction(10, "/contracts/put-transaction/put-transaction-success-request.json");
		putTransaction(11, "/contracts/put-transaction/put-transaction-child-request.json");
		var request = json("/contracts/get-transaction-sum/get-sum-response.json");

		mockMvc.perform(get("/transactions/sum/{id}", 10))
				.andExpect(status().isOk())
				.andExpect(content().json(request));
	}

	@Test
	void sumFailsWhenTransactionNotFound() throws Exception {
		mockMvc.perform(get("/transactions/sum/{id}", 999))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.detail").value("Transaction 999 not found"));
	}

	private void putTransaction(long id, String requestResource) throws Exception {
		var request = json(requestResource);

		mockMvc.perform(put("/transactions/{id}", id)
						.contentType(MediaType.APPLICATION_JSON)
						.content(request))
				.andExpect(status().isOk());
	}

	private String json(String path) throws IOException {
		try (var stream = getClass().getResourceAsStream(path)) {
			if (stream == null) {
				throw new IOException("Resource not found: " + path);
			}
			return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
		}
	}
}
