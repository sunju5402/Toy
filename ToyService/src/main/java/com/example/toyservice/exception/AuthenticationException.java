package com.example.toyservice.exception;

import com.example.toyservice.model.constants.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationException extends RuntimeException {
	private ErrorCode errorCode;
	private String errorMessage;

	public AuthenticationException(ErrorCode errorCode) {
		this.errorCode = errorCode;
		this.errorMessage = errorCode.getDescription();
	}
}
