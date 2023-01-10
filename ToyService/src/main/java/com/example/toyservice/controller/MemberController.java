package com.example.toyservice.controller;

import com.example.toyservice.dto.MemberDto;
import com.example.toyservice.model.ResponseResult;
import com.example.toyservice.model.ServiceResult;
import com.example.toyservice.model.entity.Member;
import com.example.toyservice.security.TokenProvider;
import com.example.toyservice.service.MemberService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
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
	public ResponseEntity<?> signup(@Valid @RequestBody MemberDto.Request request
		, @RequestParam("x") String x
		, @RequestParam("y") String y
		, Errors errors) {
		if (errors.hasFieldErrors()) {
			return ResponseEntity.ok(memberService.validate(errors));
		}

		ServiceResult result = memberService.register(x, y, request);

		return ResponseEntity.ok(
			new ResponseResult(result.isResult(), result.getMessage()));
	}

	@GetMapping("/email-auth")
	public ResponseEntity<?> emailAuth(@RequestParam String id) {
		String uuid = id;
		ServiceResult result = memberService.emailAuth(uuid);

		return ResponseEntity.ok(new ResponseResult(result.isResult(), result.getMessage()));
	}

	@RequestMapping("/login")
	public ResponseEntity<?> login(@Valid @RequestBody MemberDto.SignIn request) {
		Member member = memberService.authenticate(request);
		String token = tokenProvider.generateToken(member.getEmail(),
			memberService.getAuthority(member)); // roles
		return ResponseEntity.ok(new ResponseResult(true, "토큰 : " + token));
	}

	@PostMapping("/withdraw")
	public ResponseEntity<?> withdraw(@RequestBody MemberDto.SignIn request) {
		ServiceResult result = memberService.withdraw(request.getEmail(), request.getPassword());

		return ResponseEntity.ok(
			new ResponseResult(result.isResult(), result.getMessage()));
	}

}
