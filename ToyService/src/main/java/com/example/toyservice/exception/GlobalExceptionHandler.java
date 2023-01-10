package com.example.toyservice.exception;

import com.example.toyservice.dto.ErrorResponse;
import com.example.toyservice.model.constants.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
// error 발생 시, custom response로 반환
public class GlobalExceptionHandler {
	@ExceptionHandler(AuthenticationException.class)
	public ErrorResponse handleEmailException(AuthenticationException e) {
		return new ErrorResponse(e.getErrorCode(), e.getErrorMessage());
	}

	@ExceptionHandler(UsernameNotFoundException.class)
	public ErrorResponse handleUsernameException(UsernameNotFoundException e) {
		return new ErrorResponse(
			ErrorCode.MEMBER_NOT_FOUND,
			ErrorCode.MEMBER_NOT_FOUND.getDescription()
		);
	}

	@ExceptionHandler(ApiRequestException.class)
	public ErrorResponse handleUsernameException(ApiRequestException e) {
		return new ErrorResponse(
			ErrorCode.API_REQUEST_FAIL,
			ErrorCode.API_REQUEST_FAIL.getDescription()
		);
	}
}
