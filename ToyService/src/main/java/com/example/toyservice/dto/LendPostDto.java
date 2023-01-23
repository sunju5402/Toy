package com.example.toyservice.dto;

import com.example.toyservice.model.constants.LendStatus;
import com.example.toyservice.model.entity.LendPost;
import com.example.toyservice.model.entity.Member;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class LendPostDto {
	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Request {
		@NotBlank(message = "필수 입력입니다.")
		@Pattern(regexp = "^.{1,15}$",
		message = "제목은 1~15자로 입력하세요.")
		private String title;
		@NotBlank(message = "필수 입력입니다.")
		private String toyName;
		@NotBlank(message = "필수 입력입니다.")
		private String image;
		@PositiveOrZero(message = "0이상의 숫자를 입력하세요.")
		private int period;
		@NotBlank(message = "필수 입력입니다.")
		@Pattern(regexp = "^.{1,200}$")
		private String content;
		private Member lender;

		public LendPost toEntity() {
			return LendPost.builder()
				.title(title)
				.toyName(toyName)
				.image(image)
				.lendPeriod(period)
				.content(content)
				.lender(lender)
				.status(LendStatus.LEND_ING)
				.build();
		}
	}

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class Response {
		private String title;
		private String toyName;
		private String image;
		private int period;
		private String content;

		public static Response fromEntity(LendPost lendPost) {
			return Response.builder()
				.title(lendPost.getTitle())
				.toyName(lendPost.getToyName())
				.image(lendPost.getImage())
				.period(lendPost.getLendPeriod())
				.content(lendPost.getContent())
				.build();
		}
	}
}
