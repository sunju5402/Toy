package com.example.toyservice.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class ResponseResult {
	boolean result;
	String message;

	public ResponseResult(boolean result, String message) {
		this.result = result;
		this.message = message;
	}

	public ResponseResult(boolean result) {
		this.result = result;
	}
}
