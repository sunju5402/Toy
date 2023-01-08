package com.example.toyservice.dto;

import com.example.toyservice.model.constants.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {
	private ErrorCode errorCode;
	private String errorMessage;
}
