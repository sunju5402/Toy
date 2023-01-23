package com.example.toyservice.controller;

import com.example.toyservice.dto.LendPostDto;
import com.example.toyservice.dto.SellPostDto;
import com.example.toyservice.model.ResponseResult;
import com.example.toyservice.service.LendPostService;
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
public class LendPostController {
	private final LendPostService lendPostService;

	@GetMapping("/members/{memberId}/lend-posts/{postId}")
	public ResponseEntity<ResponseResult> getLendPost(
		@PathVariable Long memberId,
		@PathVariable Long postId) {
		return ResponseEntity.ok(
			new ResponseResult(lendPostService.getLendPost(memberId, postId),
				"대여글을 성공적으로 조회하였습니다."));
	}

	@GetMapping("/members/{id}/lend-posts")
	public ResponseEntity<ResponseResult> getLendPosts(@PathVariable Long id) {
		return ResponseEntity.ok(
			new ResponseResult(lendPostService.getLendPosts(id),
				"대여글을 성공적으로 조회하였습니다."));
	}

	@PostMapping("/members/{id}/lend-posts")
	public ResponseEntity<ResponseResult> lendPost(
		@PathVariable Long id,
		@Valid @RequestBody LendPostDto.Request request) {
		return ResponseEntity.ok(
			new ResponseResult(lendPostService.createLendPost(id, request)
			, "대여게시글이 생성되었습니다.")
		);
	}

	@PutMapping("/members/{memberId}/lend-posts/{postId}")
	public ResponseEntity<ResponseResult> reviseLendPost(
		@PathVariable Long memberId,
		@PathVariable Long postId,
		@Valid @RequestBody LendPostDto.Request request
	) {
		return ResponseEntity.ok(
			new ResponseResult(lendPostService.reviseLendPost(memberId, postId, request)
				, "대여게시글이 수정되었습니다.")
		);
	}

	@DeleteMapping("/members/{memberId}/lend-posts/{postId}")
	public ResponseEntity<ResponseResult> stopLendPost(
		@PathVariable Long memberId, @PathVariable Long postId
	) {
		return ResponseEntity.ok(
			new ResponseResult(lendPostService.stop(memberId, postId)
				, "대여게시글의 거래가 중지되었습니다.")
		);
	}
}