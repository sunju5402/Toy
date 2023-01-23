package com.example.toyservice.service;

import com.example.toyservice.dto.LendPostDto;
import com.example.toyservice.dto.LendPostInfo;
import com.example.toyservice.exception.AuthenticationException;
import com.example.toyservice.model.constants.ErrorCode;
import com.example.toyservice.model.constants.LendStatus;
import com.example.toyservice.model.constants.MemberStatus;
import com.example.toyservice.model.entity.LendPost;
import com.example.toyservice.model.entity.Member;
import com.example.toyservice.repository.LendPostRepository;
import com.example.toyservice.repository.MemberRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@RequiredArgsConstructor
@Service
public class LendPostService {
	private final LendPostRepository lendPostRepository;
	private final MemberRepository memberRepository;
	public LendPostDto.Response createLendPost(Long memberId, LendPostDto.Request request) {
		Member lender = memberRepository.findById(memberId)
			.orElseThrow(() -> new AuthenticationException(ErrorCode.MEMBER_NOT_FOUND));
		if (lender.getStatus() == MemberStatus.WITHDRAW) {
			throw new AuthenticationException(ErrorCode.MEMBER_WITHDRAW);
		}

		request.setLender(lender);

		return LendPostDto.Response.fromEntity(lendPostRepository.save(request.toEntity()));
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

	public void validateLender(LendPost lendPost, Long memberId) {
		if (lendPost.getLender().getId() != memberId) {
			throw new AuthenticationException(ErrorCode.NOT_MATCH_MEMBER);
		}
		if (lendPost.getLender().getStatus() == MemberStatus.WITHDRAW) {
			throw new AuthenticationException(ErrorCode.MEMBER_WITHDRAW);
		}
	}
}
