package com.example.toyservice.service;

import com.example.toyservice.exception.AuthenticationException;
import com.example.toyservice.model.ServiceResult;
import com.example.toyservice.model.constants.ErrorCode;
import com.example.toyservice.model.entity.Member;
import com.example.toyservice.model.entity.Wallet;
import com.example.toyservice.repository.MemberRepository;
import com.example.toyservice.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class WalletService {

	private final MemberRepository memberRepository;
	private final WalletRepository walletRepository;

	public ServiceResult charge(Long memberId, Long balance) {
		Wallet wallet = getWallet(memberId);
		wallet.setBalance(wallet.getBalance() + balance);
		walletRepository.save(wallet);
		return new ServiceResult(true, "충전이 완료되었습니다.");
	}

	public Member getMember(Long memberId) {
		return memberRepository.findById(memberId)
			.orElseThrow(() -> new AuthenticationException(ErrorCode.MEMBER_NOT_FOUND));
	}

	public Wallet getWallet(Long memberId) {
		return walletRepository.findByMember(getMember(memberId));
	}

	public void add(Member member) {
		Wallet wallet = Wallet.builder()
			.balance(0L)
			.member(member)
			.build();
		walletRepository.save(wallet);
	}
}
