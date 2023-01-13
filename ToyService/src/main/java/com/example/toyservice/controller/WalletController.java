package com.example.toyservice.controller;

import com.example.toyservice.dto.WalletDto;
import com.example.toyservice.model.ServiceResult;
import com.example.toyservice.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class WalletController {

	private final WalletService walletService;

	@GetMapping("/member/wallet")
	public ResponseEntity<?> memberWallet(@RequestParam("id") Long id) {
		return ResponseEntity.ok(walletService.getWallet(id));
	}

	@PostMapping("/member/wallet")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<?> memberWalletSubmit(
		@RequestParam("id") Long id
		, @RequestBody WalletDto.Request request) {
		ServiceResult result = walletService.charge(id, request.getBalance());
		return ResponseEntity.ok(
			new ServiceResult(result.isResult(), result.getMessage()));
	}
}
