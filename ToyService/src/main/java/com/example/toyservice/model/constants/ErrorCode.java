package com.example.toyservice.model.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
	EMAIL_NOT_ACTIVATE("이메일이 활성화되지 않았습니다."),
	EMAIL_ALREADY_ACTIVATE("이미 활성화가 되었습니다."),
	EMAILAUTHKEY_NOT_FOUND("잘못된 접근입니다."),
	MEMBER_WITHDRAW("탈퇴한 회원입니다."),
	EMAIL_NOT_FOUND("존재하지 않는 이메일입니다."),
	PASSWORD_NOT_MATCH("비밀번호가 일치하지 않습니다."),
	MEMBER_NOT_FOUND("회원정보가 없습니다."),
	MEMBER_ALREADY_EXIST("이미 존재하는 회원입니다."),
	ADDRESS_NOT_MATCH("현재 위치와 다른 주소입니다."),
	API_REQUEST_FAIL("api 요청 에러")
	;

	private final String description;
}