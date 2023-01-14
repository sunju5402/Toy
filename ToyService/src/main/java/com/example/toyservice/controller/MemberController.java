package com.example.toyservice.controller;

import com.example.toyservice.dto.MemberDto;
import com.example.toyservice.model.ResponseResult;
import com.example.toyservice.model.entity.Member;
import com.example.toyservice.security.TokenProvider;
import com.example.toyservice.service.MemberService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class MemberController {

	private final MemberService memberService;
	private final TokenProvider tokenProvider;

	/**
	 * javascript로 회원가입 버튼 누를 시, 현재 위치에 대한 위도와 경도 parameter로 담아서 주소 요청
	 */
	@PostMapping("/signup")
	public ResponseEntity<ResponseResult> signup(@Valid @RequestBody MemberDto.Request request
		, @RequestParam("x") String x
		, @RequestParam("y") String y) {

		return ResponseEntity.ok(
			new ResponseResult(memberService.register(x, y, request).getEmailAuthKey()
				, "발급된 인증키로 이메일 인증을 완료해주세요."));
	}

	@GetMapping("/email-auth")
	public ResponseEntity<ResponseResult> emailAuth(@RequestParam("id") String uuid) {
		return ResponseEntity.ok(new ResponseResult(memberService.emailAuth(uuid),
			"이메일 인증이 정상적으로 이루어졌습니다."));
	}

	@RequestMapping("/login")
	public ResponseEntity<ResponseResult> login(@Valid @RequestBody MemberDto.SignIn request) {
		Member member = memberService.authenticate(request);
		String token = tokenProvider.generateToken(member.getEmail(),
			memberService.getAuthority(member)); // roles
		return ResponseEntity.ok(new ResponseResult(true, "토큰 : " + token));
	}

	@DeleteMapping("/members/{id}")
	public ResponseEntity<ResponseResult> withdraw(
		@PathVariable Long id,
		@RequestBody MemberDto.SignIn request) {

		return ResponseEntity.ok(
			new ResponseResult(memberService.withdraw(id, request),
				"탈퇴되었습니다."));
	}

}
