package com.example.toyservice.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class ResponseResult {
	Object result;
	String message;

	public ResponseResult(Object result, String message) {
		this.result = result;
		this.message = message;
	}

	public ResponseResult(Object result) {
		this.result = result;
	}
}
