package com.example.toyservice.service;

import com.example.toyservice.dto.TransactionInfo;
import com.example.toyservice.exception.AuthenticationException;
import com.example.toyservice.model.constants.BorrowStatus;
import com.example.toyservice.model.constants.ErrorCode;
import com.example.toyservice.model.constants.LendStatus;
import com.example.toyservice.model.constants.MemberStatus;
import com.example.toyservice.model.constants.SellStatus;
import com.example.toyservice.model.entity.LendPost;
import com.example.toyservice.model.entity.Member;
import com.example.toyservice.model.entity.SellPost;
import com.example.toyservice.model.entity.Transaction;
import com.example.toyservice.model.entity.Wallet;
import com.example.toyservice.repository.LendPostRepository;
import com.example.toyservice.repository.MemberRepository;
import com.example.toyservice.repository.SellPostRepository;
import com.example.toyservice.repository.TransactionRepository;
import com.example.toyservice.repository.WalletRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RequiredArgsConstructor
@Slf4j
@Service
public class WalletService {

	private final WalletRepository walletRepository;
	private final SellPostRepository sellPostRepository;
	private final TransactionRepository transactionRepository;
	private final LendPostRepository lendPostRepository;
	private final MemberRepository memberRepository;

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

	public TransactionInfo purchase(Long sellPostId, String email) {
		SellPost sellPost = sellPostRepository.findById(sellPostId)
			.orElseThrow(() -> new AuthenticationException(ErrorCode.NOT_EXIST_POST));

		if (sellPost.getStatus() != SellStatus.SELL_ING) {
			throw new AuthenticationException(ErrorCode.NOT_SELL_POST);
		}

		// ?????? ?????? ???????????? ????????? ??? ??? ??????.
		if (sellPost.getSeller().getEmail().equals(email)) {
			throw new AuthenticationException(ErrorCode.NOT_TRADE_OWN_TOYS);
		}

		// ?????? ?????? wallet
		Wallet wallet = memberRepository.getByEmailAndStatus(email, MemberStatus.ING).getWallet();

		long balance = wallet.getBalance() - sellPost.getPrice();
		if (balance < 0) {
			throw new AuthenticationException(ErrorCode.INSUFFICIENT_BALANCE);
		}
		wallet.setBalance(balance);
		walletRepository.save(wallet);

		// ?????? ?????? wallet
		wallet = memberRepository.getReferenceById(sellPost.getSeller().getId()).getWallet();
		wallet.setBalance(sellPost.getPrice());
		walletRepository.save(wallet);

		Transaction sellerTransaction = Transaction.builder()
			.wallet(wallet)
			.balance(sellPost.getPrice())
			.transactionAt(LocalDateTime.now())
			.build();
		transactionRepository.save(sellerTransaction);

		sellPost.setStatus(SellStatus.SELL_COMPLETE);
		sellPost.setPurchaser(wallet.getMember());
		sellPostRepository.save(sellPost);

		Transaction purchaserTransaction = Transaction.builder()
			.wallet(wallet)
			.balance(-sellPost.getPrice())
			.transactionAt(LocalDateTime.now())
			.build();
		return TransactionInfo.fromEntity(transactionRepository.save(purchaserTransaction));
	}

	public TransactionInfo borrow(Long lendPostId, String email) {
		LendPost lendPost = lendPostRepository.findById(lendPostId)
			.orElseThrow(() -> new AuthenticationException(ErrorCode.NOT_EXIST_POST));

		if (lendPost.getStatus() != LendStatus.LEND_ING) {
			throw new AuthenticationException(ErrorCode.NOT_LEND_POST);
		}

		// ?????? ?????? ???????????? ????????? ????????? ??? ??????.
		if (lendPost.getLender().getEmail().equals(email)) {
			throw new AuthenticationException(ErrorCode.NOT_TRADE_OWN_TOYS);
		}

		// borrower wallet
		Wallet wallet = memberRepository.getByEmailAndStatus(email, MemberStatus.ING).getWallet();

		long fee = 5000;
		long balance = wallet.getBalance() - fee;
		if (balance < 0) {
			throw new AuthenticationException(ErrorCode.INSUFFICIENT_BALANCE);
		}
		wallet.setBalance(balance);
		walletRepository.save(wallet);

		// lender wallet
		wallet = memberRepository.getReferenceById(lendPost.getLender().getId()).getWallet();
		wallet.setBalance(fee);
		walletRepository.save(wallet);

		Transaction lenderTransaction = Transaction.builder()
			.wallet(wallet)
			.balance(fee)
			.transactionAt(LocalDateTime.now())
			.build();
		transactionRepository.save(lenderTransaction);

		lendPost.setStatus(LendStatus.LEND_COMPLETE);
		lendPost.setBorrowStatus(BorrowStatus.BORROW_ING);
		lendPost.setBorrower(wallet.getMember());
		lendPost.setBorrowAt(LocalDateTime.now());
		lendPostRepository.save(lendPost);

		Transaction borrowerTransaction = Transaction.builder()
			.wallet(wallet)
			.balance(-fee)
			.transactionAt(LocalDateTime.now())
			.build();
		return TransactionInfo.fromEntity(transactionRepository.save(borrowerTransaction));
	}
}
