package com.example.toyservice.controller;

import com.example.toyservice.model.ResponseResult;
import com.example.toyservice.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@PreAuthorize("hasRole('USER')")
public class TransactionController {
	private final TransactionService transactionService;

	@GetMapping("/members/{memberId}/transactions/{id}")
	public ResponseEntity<ResponseResult> getTransaction(
		@PathVariable Long memberId,
		@PathVariable Long id
	) {
		return ResponseEntity.ok(new ResponseResult(
			transactionService.getTransaction(memberId, id),
			"거래 조회를 완료하였습니다."
		));
	}

	@GetMapping("/members/{id}/transactions")
	public ResponseEntity<ResponseResult> getTransactions(
		@PathVariable Long id,
		@PageableDefault(size = 5) final Pageable pageable
	) {
		return ResponseEntity.ok(new ResponseResult(
			transactionService.getTransactions(id, pageable),
			"거래 조회를 완료하였습니다."
		));
	}
}
