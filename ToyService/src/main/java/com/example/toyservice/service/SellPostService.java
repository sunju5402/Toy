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
		List<SellPost> sellPosts = sellPostRepository.findAllBySellerId(memberId)
			.orElseThrow(() -> new AuthenticationException(ErrorCode.MEMBER_NOT_FOUND));
		return SellPostInfo.of(sellPosts);
	}

	public SellPostInfo getSellPost(Long memberId, Long postId) {
		authenticate(memberId);

		return SellPostInfo.fromEntity(
			sellPostRepository.findById(postId).orElseThrow(
				() -> new AuthenticationException(ErrorCode.NOT_EXIST_POST)
		));
	}

	public SellPostDto.Response reviseSellPost(Long memberId, Long postId,
		SellPostDto.Request request) {
		authenticate(memberId);

		SellPost sellPost = sellPostRepository.findById(postId)
			.orElseThrow( () -> new AuthenticationException(ErrorCode.NOT_EXIST_POST));
		sellPost.setTitle(request.getTitle());
		sellPost.setToyName(request.getToyName());
		sellPost.setImage(request.getImage());
		sellPost.setPrice(request.getPrice());
		sellPost.setContent(request.getContent());

		return SellPostDto.Response.fromEntity(sellPostRepository.save(sellPost));
	}

	public SellPostInfo stop(Long memberId, Long postId) {
		authenticate(memberId);

		SellPost sellPost = sellPostRepository.findById(postId)
			.orElseThrow( () -> new AuthenticationException(ErrorCode.NOT_EXIST_POST));

		sellPost.setTitle(null);
		sellPost.setToyName(null);
		sellPost.setPrice(0);
		sellPost.setImage(null);
		sellPost.setContent(null);
		sellPost.setStatus(SellStatus.SELL_STOP);

		return SellPostInfo.fromEntity(sellPostRepository.save(sellPost));
	}

	public void authenticate(Long memberId) {
		if (!sellPostRepository.existsBySellerId(memberId)) {
			throw new AuthenticationException(ErrorCode.MEMBER_NOT_FOUND);
		}
		if (memberRepository.findById(memberId).get().getStatus() == MemberStatus.WITHDRAW) {
			throw new AuthenticationException(ErrorCode.MEMBER_WITHDRAW);
		}
	}
}
