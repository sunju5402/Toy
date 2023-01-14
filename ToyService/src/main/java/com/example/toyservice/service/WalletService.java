package com.example.toyservice.service;

import com.example.toyservice.exception.AuthenticationException;
import com.example.toyservice.model.constants.ErrorCode;
import com.example.toyservice.model.entity.Member;
import com.example.toyservice.model.entity.Wallet;
import com.example.toyservice.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RequiredArgsConstructor
@Service
public class WalletService {

	private final WalletRepository walletRepository;

	public Wallet charge(Long memberId, Long balance) {
		Wallet wallet = getWallet(memberId);
		wallet.setBalance(wallet.getBalance() + balance);
		return walletRepository.save(wallet);
	}

	public Wallet getWallet(Long memberId) {
		return walletRepository.findByMemberId(memberId)
			.orElseThrow(() -> new AuthenticationException(ErrorCode.MEMBER_NOT_FOUND));
	}

	public void add(Member member) {
		Wallet wallet = Wallet.builder()
			.balance(0L)
			.member(member)
			.build();
		walletRepository.save(wallet);
	}

	public void delete(Long memberId) {
		walletRepository.delete(getWallet(memberId));
	}
}
