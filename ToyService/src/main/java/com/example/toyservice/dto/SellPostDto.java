package com.example.toyservice.dto;

import com.example.toyservice.model.constants.SellStatus;
import com.example.toyservice.model.entity.Member;
import com.example.toyservice.model.entity.SellPost;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class SellPostDto {
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
		@PositiveOrZero(message = "0이상의 금액을 입력하세요")
		private long price;
		@NotBlank(message = "필수 입력입니다.")
		@Pattern(regexp = "^.{1,200}$")
		private String content;
		private Member seller;

		public SellPost toEntity() {
			return SellPost.builder()
				.title(title)
				.toyName(toyName)
				.image(image)
				.price(price)
				.content(content)
				.seller(seller)
				.status(SellStatus.SELL_ING)
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
		private long price;
		private String content;

		public static Response fromEntity(SellPost sellPost) {
			return Response.builder()
				.title(sellPost.getTitle())
				.toyName(sellPost.getToyName())
				.image(sellPost.getImage())
				.price(sellPost.getPrice())
				.content(sellPost.getContent())
				.build();
		}
	}
}
