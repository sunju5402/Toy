package com.example.toyservice.controller;

import com.example.toyservice.dto.WalletDto;
import com.example.toyservice.model.ResponseResult;
import com.example.toyservice.service.WalletService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class WalletController {

	private final WalletService walletService;

	@GetMapping("/members/{id}/wallets")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<ResponseResult> memberWallet(
		@PathVariable Long id) {
		return ResponseEntity.ok(
			new ResponseResult(walletService.getWallet(id),
				"wallet 정보를 성공적으로 조회했습니다."));
	}

	@PutMapping("/members/{id}/wallets")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<ResponseResult> chargeBalance(
		@PathVariable Long id
		, @Valid @RequestBody WalletDto.Request request) {
		return ResponseEntity.ok(
			new ResponseResult(WalletDto.Response.fromEntity(
				walletService.charge(id, request.getBalance()))
				, "충전이 완료되었습니다."));
	}
}
