package com.example.toyservice.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class ValidateResult {
	String errorField;
	String message;

	public ValidateResult(String errorField, String message) {
		this.errorField = errorField;
		this.message = message;
	}
}
