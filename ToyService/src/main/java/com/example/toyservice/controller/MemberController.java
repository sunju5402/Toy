package com.example.toyservice.controller;

import com.example.toyservice.dto.MemberDto;
import com.example.toyservice.model.ResponseResult;
import com.example.toyservice.model.ServiceResult;
import com.example.toyservice.model.entity.Member;
import com.example.toyservice.security.TokenProvider;
import com.example.toyservice.service.MemberService;
import java.security.Principal;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
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


	@PostMapping("/signup")
	public ResponseEntity<?> signup(@Valid @RequestBody MemberDto.Request request
									, Errors errors) {
		if (errors.hasFieldErrors()) {
			return ResponseEntity.ok(memberService.validate(errors));
		}

		ServiceResult result = memberService.register(request);

		return ResponseEntity.ok(
			new ResponseResult(result.isResult(), result.getMessage()));
	}

	@GetMapping("/email-auth")
	public ResponseEntity<?> emailAuth(Model model, @RequestParam String id) {
		String uuid = id;

		ServiceResult result = memberService.emailAuth(uuid);
		model.addAttribute("result", result);

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
	public ResponseEntity<?> memberWithdrawSubmit(@RequestBody MemberDto.SignIn request) {
		ServiceResult result = memberService.withdraw(request.getEmail(), request.getPassword());

		return ResponseEntity.ok(
			new ResponseResult(result.isResult(), result.getMessage()));
	}

}
