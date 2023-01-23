package com.example.toyservice.controller;

import com.example.toyservice.dto.WalletDto;
import com.example.toyservice.model.ResponseResult;
import com.example.toyservice.service.WalletService;
import java.security.Principal;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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

	@PostMapping("/sell-posts/{id}/purchase")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<ResponseResult> purchase(
		@PathVariable Long id,
		@AuthenticationPrincipal User user
	) {
		return ResponseEntity.ok(
			new ResponseResult(walletService.purchase(id, user.getUsername())
			, "거래가 완료되었습니다."));
	}

	@PostMapping("/lend-posts/{id}/borrow")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<ResponseResult> borrow(
		@PathVariable Long id,
		@AuthenticationPrincipal User user
	) {
		return ResponseEntity.ok(
			new ResponseResult(walletService.borrow(id, user.getUsername())
				, "대여가 완료되었습니다."));
	}
}
