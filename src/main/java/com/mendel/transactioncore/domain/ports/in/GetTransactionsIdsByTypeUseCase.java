package com.mendel.transactioncore.domain.ports.in;

import java.util.List;

public interface GetTransactionsIdsByTypeUseCase {

	List<Long> getByType(String type);
}
