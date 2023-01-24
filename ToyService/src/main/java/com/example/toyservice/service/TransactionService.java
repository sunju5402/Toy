package com.example.toyservice.service;

import com.example.toyservice.dto.TransactionInfo;
import com.example.toyservice.exception.AuthenticationException;
import com.example.toyservice.model.constants.ErrorCode;
import com.example.toyservice.model.constants.MemberStatus;
import com.example.toyservice.model.entity.Member;
import com.example.toyservice.model.entity.Transaction;
import com.example.toyservice.repository.MemberRepository;
import com.example.toyservice.repository.TransactionRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@RequiredArgsConstructor
@Service
public class TransactionService {

	private final TransactionRepository transactionRepository;
	private final MemberRepository memberRepository;

	public TransactionInfo getTransaction(Long memberId, Long id) {
		Transaction transaction = transactionRepository.findById(id)
			.orElseThrow(() -> new AuthenticationException(ErrorCode.NOT_EXIST_TRANSACTION));

		if (transaction.getWallet().getMember().getId() != memberId) {
			throw new AuthenticationException(ErrorCode.NOT_MATCH_MEMBER);
		}
		if (transaction.getWallet().getMember().getStatus() == MemberStatus.WITHDRAW) {
			throw new AuthenticationException(ErrorCode.MEMBER_WITHDRAW);
		}

		return TransactionInfo.fromEntity(transaction);
	}

	// page 정보까지 담고 싶으면 return type : Page
	public List<TransactionInfo> getTransactions(Long memberId, Pageable pageable) {
		Member member = getMember(memberId);
		List<Transaction> transactions = transactionRepository.findAllByWalletId(
			member.getWallet().getId(), pageable);
		if (CollectionUtils.isEmpty(transactions)) {
			throw new AuthenticationException(ErrorCode.NOT_EXIST_TRANSACTION);
		}

		return TransactionInfo.of(transactions);
	}

	public Member getMember(Long id) {
		return memberRepository.findById(id)
			.orElseThrow(() -> new AuthenticationException(ErrorCode.MEMBER_NOT_FOUND));
	}
}