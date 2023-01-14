package com.example.toyservice.dto;

import com.example.toyservice.model.constants.MemberStatus;
import com.example.toyservice.model.entity.Member;
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
		@Pattern(regexp = "^[가-힣]{2,20}$",
		message = "이름은 공백이 없이 한글로 2~20자로 입력해주세요.")
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
		private boolean admin;
		private String emailAuthKey;
		private boolean rejoin;

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
				.admin(admin)
				.status(MemberStatus.REQ)
				.regDt(LocalDateTime.now())
				.emailAuth(false)
				.emailAuthKey(emailAuthKey)
				.rejoin(rejoin)
				.build();
		}
	}

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class Response {
		private String name;
		private String email;

		public static Response fromEntity(Member member) {
			return Response.builder()
				.name(member.getName())
				.email(member.getEmail())
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

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class Withdraw {
		private String name;
		private String email;
		private String password;
		private String nickname;
		private String phone;
		private String zipcode;
		private String address1;
		private String address2;
		private MemberStatus status;
		private LocalDateTime regDt;
		private boolean emailAuth;
		private String emailAuthKey;
		private LocalDateTime emailAuthDt;
		private WalletDto.Response wallet;

		public static Withdraw fromEntity(Member member) {
			return Withdraw.builder()
				.name(member.getName())
				.email(member.getEmail())
				.password(member.getPassword())
				.nickname(member.getNickname())
				.phone(member.getPhone())
				.zipcode(member.getZipcode())
				.address1(member.getAddress1())
				.address2(member.getAddress2())
				.status(member.getStatus())
				.regDt(member.getRegDt())
				.emailAuth(member.isEmailAuth())
				.emailAuthKey(member.getEmailAuthKey())
				.emailAuthDt(member.getEmailAuthDt())
				.wallet(WalletDto.Response.fromEntity(member.getWallet()))
				.build();
		}
	}
}
