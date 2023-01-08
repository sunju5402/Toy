package com.example.toyservice.service;

import com.example.toyservice.components.MailComponents;
import com.example.toyservice.dto.MemberDto;
import com.example.toyservice.exception.AuthenticationException;
import com.example.toyservice.model.ValidateResult;
import com.example.toyservice.model.constants.ErrorCode;
import com.example.toyservice.model.constants.MemberStatus;
import com.example.toyservice.model.entity.Member;
import com.example.toyservice.model.ServiceResult;
import com.example.toyservice.model.constants.Authority;
import com.example.toyservice.repository.MemberRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

@Slf4j
@RequiredArgsConstructor
@Service
public class MemberService implements UserDetailsService {

	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;
	private final MailComponents mailComponents;


	public ServiceResult register(MemberDto.Request member) {
		boolean exists = memberRepository.existsByEmail(member.getEmail());
		if (exists) {
			return new ServiceResult(false, "이미 존재하는 회원 정보입니다.");
		}

		String uuid = UUID.randomUUID().toString();

		member.setPassword(passwordEncoder.encode(member.getPassword()));
		member.setEmailAuthKey(uuid);
		memberRepository.save(member.toEntity());


		String email = member.getEmail();
		String subject = "ToyService 가입을 축하드립니다. ";
		String text = "<p>ToyService 가입을 축하드립니다."
			+ "<p><p>아래 링크를 클릭하셔서 가입을 완료 하세요.</p>"
			+ "<div><a target='_blank' href='http://localhost:8080/email-auth?id=" + uuid + "'> 가입 완료 </a></div>";
		mailComponents.sendMail(email, subject, text);

		return new ServiceResult(true, "emailAuthKey: " +
			uuid + "이메일 인증을 해주세요.");
	}

	public ServiceResult emailAuth(String emailAuthKey) {
		Optional<Member> optionalMember = memberRepository.findByEmailAuthKey(emailAuthKey);
		if (!optionalMember.isPresent()) {
			return new ServiceResult(false, "잘못된 접근입니다.");
		}

		Member member = optionalMember.get();

		if (member.isEmailAuthYn()) {
			return new ServiceResult(false, "이미 활성화가 되었습니다.");
		}

		member.setStatus(MemberStatus.ING);
		member.setEmailAuthYn(true);
		member.setEmailAuthDt(LocalDateTime.now());
		memberRepository.save(member);

		return new ServiceResult(true, "이메일 인증이 정상적으로 이루어졌습니다.");
	}

	public Member authenticate(MemberDto.SignIn singIn) {
		Member member = memberRepository.findByEmail(singIn.getEmail()).orElse(null);

		if (member == null) {
			throw new AuthenticationException(ErrorCode.EMAIL_NOT_FOUND);
		}

		if (MemberStatus.REQ.equals(member.getStatus())) {
			throw new AuthenticationException(ErrorCode.EMAIL_NOT_ACTIVATE);
		}

		if (MemberStatus.WITHDRAW.equals(member.getStatus())) {
			throw new AuthenticationException(ErrorCode.MEMBER_WITHDRAW);
		}

		if (!passwordEncoder.matches(singIn.getPassword(), member.getPassword())) {
			throw new AuthenticationException(ErrorCode.PASSWORD_NOT_MATCH);
		}

		return member;
	}

	public ServiceResult withdraw(String email, String password) {
		Member member = memberRepository.findByEmail(email).orElse(null);
		if (member == null) {
			throw  new AuthenticationException(ErrorCode.MEMBER_NOT_FOUND);
		}

		if (!passwordEncoder.matches(password, member.getPassword())) {
			throw new AuthenticationException(ErrorCode.PASSWORD_NOT_MATCH);
		}

		member.setName("사용자 정보가 없습니다.");
		member.setPhone("");
		member.setPassword("");
		member.setRegDt(null);
		member.setUpdatedAt(null);
		member.setEmailAuthYn(false);
		member.setEmailAuthDt(null);
		member.setEmailAuthKey("");
		member.setStatus(MemberStatus.WITHDRAW);
//		member.setResetPasswordKey("");
//		member.setResetPasswordLimitDt(null);
//		member.setZipcode("");
//		member.setAddr("");
//		member.setAddrDetail("");
		memberRepository.save(member);

		return new ServiceResult(true, "탈퇴되었습니다.");
	}

	public List<ValidateResult> validate(Errors errors) {
		List<ValidateResult> list = new ArrayList<>();
		ValidateResult result;
		for (FieldError e : errors.getFieldErrors()) {
			result = new ValidateResult(e.getField(), e.getDefaultMessage());
			list.add(result);
		}
		return list;
	}

//	public Member getMemberByEmail(String email) {
//		Optional<Member> optionalMember = memberRepository.findByEmail(email);
//		if (!optionalMember.isPresent()) {
//			return null;
//		}
//		return optionalMember.get();
//	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//		Member member = memberRepository.findByEmail(username)
//			.orElseThrow(() -> new UsernameNotFoundException("회원 정보가 없습니다."));
		Member member = memberRepository.findByEmail(username).orElse(null);
		if (member == null) {
			throw new UsernameNotFoundException("회원 정보가 없습니다.");
		}

		List<GrantedAuthority> grantedAuthorities = getAuthority(member);

		return new User(member.getEmail(), member.getPassword(), grantedAuthorities);
	}

	public List<GrantedAuthority> getAuthority(Member member) {
		List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
		grantedAuthorities.add(new SimpleGrantedAuthority(Authority.ROLE_USER.toString()));

		if (member.isAdmin()) {
			grantedAuthorities.add(new SimpleGrantedAuthority(Authority.ROLE_ADMIN.toString()));
		}

		return grantedAuthorities;
	}
}
