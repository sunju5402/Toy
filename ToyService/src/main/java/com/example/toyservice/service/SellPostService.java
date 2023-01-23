package com.example.toyservice.service;

import com.example.toyservice.dto.SellPostDto;
import com.example.toyservice.dto.SellPostDto.Request;
import com.example.toyservice.dto.SellPostInfo;
import com.example.toyservice.exception.AuthenticationException;
import com.example.toyservice.model.constants.ErrorCode;
import com.example.toyservice.model.constants.MemberStatus;
import com.example.toyservice.model.constants.SellStatus;
import com.example.toyservice.model.entity.Member;
import com.example.toyservice.model.entity.SellPost;
import com.example.toyservice.repository.MemberRepository;
import com.example.toyservice.repository.SellPostRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@RequiredArgsConstructor
@Service
public class SellPostService {
	private final SellPostRepository sellPostRepository;
	private final MemberRepository memberRepository;
	public SellPostDto.Response createSellPost(Long memberId, Request request) {
		Member seller = memberRepository.findById(memberId)
			.orElseThrow(() -> new AuthenticationException(ErrorCode.MEMBER_NOT_FOUND));
		if (seller.getStatus() == MemberStatus.WITHDRAW) {
			throw new AuthenticationException(ErrorCode.MEMBER_WITHDRAW);
		}

		request.setSeller(seller);

		return SellPostDto.Response.fromEntity(sellPostRepository.save(request.toEntity()));
	}

	public List<SellPostInfo> getSellPosts(Long memberId) {
		List<SellPost> sellPosts = sellPostRepository.findAllBySellerId(memberId);

		if (CollectionUtils.isEmpty(sellPosts)) {
			throw new AuthenticationException(ErrorCode.NOT_EXIST_POST);

		}
		validateSeller(sellPosts.get(0), memberId);

		return SellPostInfo.of(sellPosts);
	}

	public SellPostInfo getSellPost(Long memberId, Long postId) {
		SellPost sellPost = sellPostRepository.findById(postId).orElseThrow(
			() -> new AuthenticationException(ErrorCode.NOT_EXIST_POST));

		validateSeller(sellPost, memberId);

		return SellPostInfo.fromEntity(sellPost);
	}

	public SellPostDto.Response reviseSellPost(Long memberId, Long postId,
		SellPostDto.Request request) {
		SellPost sellPost = sellPostRepository.findById(postId)
			.orElseThrow( () -> new AuthenticationException(ErrorCode.NOT_EXIST_POST));

		validateSeller(sellPost, memberId);

		sellPost.setTitle(request.getTitle());
		sellPost.setToyName(request.getToyName());
		sellPost.setImage(request.getImage());
		sellPost.setPrice(request.getPrice());
		sellPost.setContent(request.getContent());

		return SellPostDto.Response.fromEntity(sellPostRepository.save(sellPost));
	}

	public SellPostInfo stop(Long memberId, Long postId) {
		SellPost sellPost = sellPostRepository.findById(postId)
			.orElseThrow( () -> new AuthenticationException(ErrorCode.NOT_EXIST_POST));

		validateSeller(sellPost, memberId);

		sellPost.setStatus(SellStatus.SELL_STOP);

		return SellPostInfo.fromEntity(sellPostRepository.save(sellPost));
	}

	public void validateSeller(SellPost sellPost, Long memberId) {
		if (sellPost.getSeller().getId() != memberId) {
			throw new AuthenticationException(ErrorCode.NOT_MATCH_MEMBER);
		}
		if (sellPost.getSeller().getStatus() == MemberStatus.WITHDRAW) {
			throw new AuthenticationException(ErrorCode.MEMBER_WITHDRAW);
		}
	}
}
