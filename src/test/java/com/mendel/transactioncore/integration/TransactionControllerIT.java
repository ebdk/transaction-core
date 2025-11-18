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

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class TransactionControllerIT {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void createsTransaction() throws Exception {
		var request = json("/contracts/post-transaction/create-transaction-success-request.json");
		var expectedResponse = json("/contracts/post-transaction/create-transaction-success-response.json");

		mockMvc.perform(post("/transactions")
						.contentType(MediaType.APPLICATION_JSON)
						.content(request))
				.andExpect(status().isCreated())
				.andExpect(content().json(expectedResponse));
	}

	@Test
	void failsWhenParentDoesNotExist() throws Exception {
		var request = json("/contracts/post-transaction/create-transaction-missing-parent-request.json");

		mockMvc.perform(post("/transactions")
						.contentType(MediaType.APPLICATION_JSON)
						.content(request))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.detail").value("Transaction 999 not found"));
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
