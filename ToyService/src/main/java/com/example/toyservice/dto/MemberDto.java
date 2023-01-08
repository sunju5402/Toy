package com.example.toyservice.dto;

import com.example.toyservice.model.entity.Member;
import com.example.toyservice.model.constants.MemberStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


public class MemberDto {

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class Request {
		@NotBlank(message = "필수 입력입니다.")
		@Email
		private String email;
		@NotBlank(message = "필수 입력입니다.")
		private String name;
		@NotBlank(message = "필수 입력입니다.")
		@Pattern(regexp = "^[ㄱ-ㅎ가-힣a-z0-9-_]{3,10}$",
			message = "닉네임은 특수문자를 제외한 3~10자로 입력해주세요.")
		private String nickname;
		@NotBlank@NotBlank(message = "필수 입력입니다.")
		@Pattern(regexp = "(?=.*[0-9])(?=.*[a-z|A-Z])(?=.*\\W)(?=\\S+$).{8,16}",
			message = "비밀번호는 8~16자이고, 적어도 영문 대 or 소문자, 숫자, 특수문자를 한 개 이상 입력하세요.")
		private String password;
		@NotBlank(message = "필수 입력입니다.")
		private String zipcode;
		@NotBlank(message = "필수 입력입니다.")
		private String address1;
		private String address2;
		@NotBlank(message = "필수 입력입니다.")
		@Pattern(regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$",message = "전화번호 양식과 맞지 않습니다. "
			+ "xx(x)-xxx(x)-xxxx")
		private String phone;
		// primitive boolean 타입이 is로 시작할때 is를 생략하고 json에서 넘어와 제대로 변환되지 않음.
		// Boolean 래퍼 타입으로 바꿔줘도 매핑됨.
		@JsonProperty("isAdmin")
		private boolean isAdmin;

		private boolean emailAuthYn;
		private String emailAuthKey;

		public Member toEntity() {
			return Member.builder()
				.email(email)
				.name(name)
				.nickname(nickname)
				.password(password)
				.zipcode(zipcode)
				.address1(address1)
				.address2(address2)
				.phone(phone)
				.isAdmin(isAdmin)
				.status(MemberStatus.REQ)
				.regDt(LocalDateTime.now())
				.emailAuthYn(false)
				.emailAuthKey(emailAuthKey)
				.build();
		}
	}

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class SignIn {
		private String email;
		private String password;
	}
}
