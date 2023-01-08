package com.example.toyservice.model.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
	EMAIL_NOT_ACTIVATE("이메일이 활성화되지 않았습니다."),
	MEMBER_WITHDRAW("탈퇴한 회원입니다."),
	EMAIL_NOT_FOUND("존재하지 않는 이메일입니다."),
	PASSWORD_NOT_MATCH("비밀번호가 일치하지 않습니다."),
	MEMBER_NOT_FOUND("회원정보가 없습니다.")
	;

	private final String description;
}
