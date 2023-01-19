package com.example.toyservice.dto;

import com.example.toyservice.model.entity.Member;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class RevisionMember {
	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Request {
		@NotBlank(message = "필수 입력입니다.")
		@Pattern(regexp = "^[ㄱ-ㅎ가-힣a-z0-9-_]{3,10}$",
			message = "닉네임은 특수문자를 제외한 3~10자로 입력해주세요.")
		private String nickname;

		@NotBlank(message = "필수 입력입니다.")
		@Pattern(regexp = "(?=.*[0-9])(?=.*[a-z|A-Z])(?=.*\\W)(?=\\S+$).{8,16}",
			message = "비밀번호는 8~16자이고, 적어도 영문 대 or 소문자, 숫자, 특수문자를 한 개 이상 입력하세요.")
		private String password;

		@NotBlank(message = "필수 입력입니다.")
		@Pattern(regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$",message = "전화번호 양식과 맞지 않습니다. "
			+ "xx(x)-xxx(x)-xxxx")
		private String phone;
	}

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class Response {
		private String nickname;
		private String phone;

		public static Response fromEntity(Member member) {
			return Response.builder()
				.nickname(member.getNickname())
				.phone(member.getPhone())
				.build();
		}
	}

}
