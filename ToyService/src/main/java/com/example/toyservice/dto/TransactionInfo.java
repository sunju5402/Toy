package com.example.toyservice.dto;

import com.example.toyservice.model.entity.Transaction;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionInfo {
	private long balance;
	private LocalDateTime transactionAt;

	public static TransactionInfo fromEntity(Transaction transaction) {
		return TransactionInfo.builder()
			.balance(transaction.getBalance())
			.transactionAt(transaction.getTransactionAt())
			.build();
	}
}
