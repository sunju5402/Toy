package com.example.toyservice.service;

import com.example.toyservice.dto.LendPostDto;
import com.example.toyservice.dto.LendPostInfo;
import com.example.toyservice.dto.TransactionInfo;
import com.example.toyservice.exception.AuthenticationException;
import com.example.toyservice.model.constants.BorrowStatus;
import com.example.toyservice.model.constants.ErrorCode;
import com.example.toyservice.model.constants.LendStatus;
import com.example.toyservice.model.constants.MemberStatus;
import com.example.toyservice.model.entity.LendPost;
import com.example.toyservice.model.entity.Member;
import com.example.toyservice.model.entity.Transaction;
import com.example.toyservice.model.entity.Wallet;
import com.example.toyservice.repository.LendPostRepository;
import com.example.toyservice.repository.MemberRepository;
import com.example.toyservice.repository.TransactionRepository;
import com.example.toyservice.repository.WalletRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class LendPostService {
	private final LendPostRepository lendPostRepository;
	private final MemberRepository memberRepository;
	private final WalletRepository walletRepository;
	private final TransactionRepository transactionRepository;

	public LendPostDto.Response createLendPost(Long memberId, LendPostDto.Request request) {
		Member lender = memberRepository.findById(memberId)
			.orElseThrow(() -> new AuthenticationException(ErrorCode.MEMBER_NOT_FOUND));
		if (lender.getStatus() == MemberStatus.WITHDRAW) {
			throw new AuthenticationException(ErrorCode.MEMBER_WITHDRAW);
		}

		request.setLender(lender);

		return LendPostDto.Response.fromEntity(lendPostRepository.save(request.toEntity()));
	}

	@Scheduled(cron = "0 0 0 * * *") // 매일 자정
	public void updateBorrowPeriod() {
		List<LendPost> lendPosts = lendPostRepository.findAllByBorrowStatus(BorrowStatus.BORROW_ING);
		if (CollectionUtils.isEmpty(lendPosts)) {
			throw new AuthenticationException(ErrorCode.NOT_EXIST_BORROW_TOY);
		}

		for (LendPost post : lendPosts) {
			post.setBorrowPeriod(post.getBorrowPeriod() + 1);
			// 연체 시, 연체 기간 수정
			if (post.getBorrowPeriod() > post.getLendPeriod()) { // 빌린 기간이 대여 기간 넘어가면 연체
				post.setOverduePeriod(post.getOverduePeriod() + 1);
			}
			lendPostRepository.save(post);
		}
	}

	public List<LendPostInfo> getLendPosts(Long memberId) {
		List<LendPost> lendPosts = lendPostRepository.findAllByLenderId(memberId);

		if (CollectionUtils.isEmpty(lendPosts)) {
			throw new AuthenticationException(ErrorCode.NOT_EXIST_POST);
		}
		validateLender(lendPosts.get(0), memberId);

		return LendPostInfo.of(lendPosts);
	}

	public LendPostInfo getLendPost(Long memberId, Long postId) {
		LendPost lendPost = lendPostRepository.findById(postId).orElseThrow(
			() -> new AuthenticationException(ErrorCode.NOT_EXIST_POST));

		validateLender(lendPost, memberId);

		return LendPostInfo.fromEntity(lendPost);
	}

	public LendPostDto.Response reviseLendPost(Long memberId, Long postId,
		LendPostDto.Request request) {
		LendPost lendPost = lendPostRepository.findById(postId)
			.orElseThrow( () -> new AuthenticationException(ErrorCode.NOT_EXIST_POST));

		validateLender(lendPost, memberId);

		lendPost.setTitle(request.getTitle());
		lendPost.setToyName(request.getToyName());
		lendPost.setImage(request.getImage());
		lendPost.setLendPeriod(request.getPeriod());
		lendPost.setContent(request.getContent());

		return LendPostDto.Response.fromEntity(lendPostRepository.save(lendPost));
	}

	public LendPostInfo stop(Long memberId, Long postId) {
		LendPost lendPost = lendPostRepository.findById(postId)
			.orElseThrow( () -> new AuthenticationException(ErrorCode.NOT_EXIST_POST));

		validateLender(lendPost, memberId);

		lendPost.setStatus(LendStatus.LEND_STOP);

		return LendPostInfo.fromEntity(lendPostRepository.save(lendPost));
	}

	public TransactionInfo returnToy(Long postId, String email) {
		LendPost lendPost = lendPostRepository.findById(postId)
			.orElseThrow( () -> new AuthenticationException(ErrorCode.NOT_EXIST_POST));
		Member borrower = memberRepository.getByEmailAndStatus(email, MemberStatus.ING);
		Member lender = memberRepository.findById(lendPost.getLender().getId())
				.orElseThrow(() -> new AuthenticationException(ErrorCode.MEMBER_NOT_FOUND));

		validateBorrower(lendPost, borrower.getId());

		if (lendPost.getStatus() != LendStatus.LEND_COMPLETE ||
		lendPost.getBorrowStatus() != BorrowStatus.BORROW_ING) {
			throw new AuthenticationException(ErrorCode.NOT_BORROW_POST);
		}

		Wallet lenderWallet = lender.getWallet();
		Wallet borrowerWallet = borrower.getWallet();

		double fee = 5000;
		long balance = lenderWallet.getBalance();
		if (lendPost.getOverduePeriod() > 0) { // 연체되었으면 대여할 때 포인트의 25% 차감 후 환불
			fee *= 0.75;
		}

		balance -= (long)fee;
		if (balance < 0) {
			throw new AuthenticationException(ErrorCode.INSUFFICIENT_BALANCE);
		}

		lenderWallet.setBalance(balance);
		walletRepository.save(lenderWallet);
		borrowerWallet.setBalance(borrowerWallet.getBalance() + (long)fee);
		walletRepository.save(borrowerWallet);

		lendPost.setBorrowStatus(BorrowStatus.BORROW_END);

		Transaction transaction = Transaction.builder()
			.wallet(lenderWallet)
			.balance(-(long)fee)
			.transactionAt(LocalDateTime.now())
			.build();
		transactionRepository.save(transaction);

		transaction = Transaction.builder()
			.wallet(borrowerWallet)
			.balance((long)fee)
			.transactionAt(LocalDateTime.now())
			.build();
		return TransactionInfo.fromEntity(transactionRepository.save(transaction));
	}

	public void validateLender(LendPost lendPost, Long memberId) {
		if (lendPost.getLender().getId() != memberId) {
			throw new AuthenticationException(ErrorCode.NOT_MATCH_MEMBER);
		}
		if (lendPost.getLender().getStatus() == MemberStatus.WITHDRAW) {
			throw new AuthenticationException(ErrorCode.MEMBER_WITHDRAW);
		}
	}

	public void validateBorrower(LendPost lendPost, Long memberId) {
		if (lendPost.getBorrowStatus() == null) {
			throw new AuthenticationException(ErrorCode.NOT_EXIST_BORROW_TOY);
		}
		if (lendPost.getBorrower().getId() != memberId) {
			throw new AuthenticationException(ErrorCode.NOT_MATCH_MEMBER);
		}
		if (lendPost.getBorrower().getStatus() == MemberStatus.WITHDRAW) {
			throw new AuthenticationException(ErrorCode.MEMBER_WITHDRAW);
		}
	}
}
