package com.example.toyservice.dto;

import com.example.toyservice.model.entity.Wallet;
import java.time.LocalDateTime;
import javax.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class WalletDto {

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Request {
		@PositiveOrZero(message = "0이상의 금액을 입력하세요")
		private Long balance;
	}

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class Response {
		private Long balance;
		private LocalDateTime updatedAt;

		public static Response fromEntity(Wallet wallet) {
			return Response.builder()
				.balance(wallet.getBalance())
				.updatedAt(wallet.getUpdatedAt())
				.build();
		}
	}
}
