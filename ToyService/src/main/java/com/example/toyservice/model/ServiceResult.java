package com.example.toyservice.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class ServiceResult {
	boolean result;
	String message;

	public ServiceResult(boolean result, String message) {
		this.result = result;
		this.message = message;
	}

	public ServiceResult(boolean result) {
		this.result = result;
	}
}
