package com.example.toyservice.dto;

import com.example.toyservice.model.entity.Transaction;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.CollectionUtils;

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

	public static List<TransactionInfo> of(List<Transaction> transactions) {
		if (CollectionUtils.isEmpty(transactions)) {
			return null;
		}
		// page 정보까지 넘기고 싶으면 new PageImpl<>()
		return transactions.stream()
			.map(TransactionInfo::fromEntity)
			.collect(Collectors.toList());
	}
}
