package com.example.toyservice.controller;

import com.example.toyservice.dto.SellPostDto;
import com.example.toyservice.model.ResponseResult;
import com.example.toyservice.service.SellPostService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@PreAuthorize("hasRole('USER')")
public class SellPostController {
	private final SellPostService sellPostService;

	@GetMapping("/members/{memberId}/posts/{postId}")
	public ResponseEntity<ResponseResult> getSellPost(
		@PathVariable Long memberId,
		@PathVariable Long postId) {
		return ResponseEntity.ok(
			new ResponseResult(sellPostService.getSellPost(memberId, postId),
				"판매글을 성공적으로 조회하였습니다."));
	}

	@GetMapping("/members/{id}/posts")
	public ResponseEntity<ResponseResult> getSellPosts(@PathVariable Long id) {
		return ResponseEntity.ok(
			new ResponseResult(sellPostService.getSellPosts(id),
				"판매글을 성공적으로 조회하였습니다."));
	}

	@PostMapping("/members/{id}/posts")
	public ResponseEntity<ResponseResult> sellPost(
		@PathVariable Long id,
		@Valid @RequestBody SellPostDto.Request request) {
		return ResponseEntity.ok(
			new ResponseResult(sellPostService.createSellPost(id, request)
			, "판매게시글이 생성되었습니다.")
		);
	}

	@PutMapping("/members/{memberId}/posts/{postId}")
	public ResponseEntity<ResponseResult> reviseSellPost(
		@PathVariable Long memberId,
		@PathVariable Long postId,
		@Valid @RequestBody SellPostDto.Request request
	) {
		return ResponseEntity.ok(
			new ResponseResult(sellPostService.reviseSellPost(memberId, postId, request)
				, "판매게시글이 수정되었습니다.")
		);
	}

	@DeleteMapping("/members/{memberId}/posts/{postId}")
	public ResponseEntity<ResponseResult> stopSellPost(
		@PathVariable Long memberId, @PathVariable Long postId
	) {
		return ResponseEntity.ok(
			new ResponseResult(sellPostService.stop(memberId, postId)
				, "판매게시글의 거래가 중지되었습니다.")
		);
	}
}
