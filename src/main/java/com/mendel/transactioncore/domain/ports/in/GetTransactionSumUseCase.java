package com.mendel.transactioncore.domain.ports.in;

import java.math.BigDecimal;

public interface GetTransactionSumUseCase {

	BigDecimal getSum(long transactionId);
}
