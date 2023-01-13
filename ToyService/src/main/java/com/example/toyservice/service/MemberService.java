package com.example.toyservice.service;

import com.example.toyservice.components.GpsComponents;
import com.example.toyservice.components.MailComponents;
import com.example.toyservice.dto.MemberDto;
import com.example.toyservice.exception.AuthenticationException;
import com.example.toyservice.model.ServiceResult;
import com.example.toyservice.model.constants.Authority;
import com.example.toyservice.model.constants.ErrorCode;
import com.example.toyservice.model.constants.MemberStatus;
import com.example.toyservice.model.entity.Member;
import com.example.toyservice.repository.MemberRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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

@Slf4j
@RequiredArgsConstructor
@Service
public class MemberService implements UserDetailsService {

	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;
	private final MailComponents mailComponents;
	private final GpsComponents gpsComponents;


	public ServiceResult register(String x, String y, MemberDto.Request member) {
		boolean existsByEmail = memberRepository.existsByEmail(member.getEmail());
		if (existsByEmail) {
			throw new AuthenticationException(ErrorCode.MEMBER_ALREADY_EXIST);
		}

		boolean existsByNickname = memberRepository.existsByNickname(member.getNickname());
		if (existsByNickname) {
			throw new AuthenticationException(ErrorCode.NICKNAME_ALREADY_EXIST);
		}

		String[] address = gpsComponents.latAndLonToAddr(x, y).split(" ");
		String zipcode = address[0]; // 우편번호에는 시, 군, 자치구를 구별하는 코드가 포함.
		String dongName = address[1];
		if (!member.getZipcode().equals(zipcode) || !member.getAddress1().contains(dongName)) {
			throw new AuthenticationException(ErrorCode.ADDRESS_NOT_MATCH);
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
			uuid + " 이메일 인증을 해주세요.");
	}

	public ServiceResult emailAuth(String emailAuthKey) {
		Member member = memberRepository.findByEmailAuthKey(emailAuthKey)
			.orElseThrow(() -> new AuthenticationException(ErrorCode.EMAILAUTHKEY_NOT_FOUND));

		if (member.isEmailAuthYn()) {
			throw new AuthenticationException(ErrorCode.EMAIL_ALREADY_ACTIVATE);
		}

		member.setStatus(MemberStatus.ING);
		member.setEmailAuthYn(true);
		member.setEmailAuthDt(LocalDateTime.now());
		memberRepository.save(member);

		return new ServiceResult(true, "이메일 인증이 정상적으로 이루어졌습니다.");
	}

	public Member authenticate(MemberDto.SignIn singIn) {
		Member member = memberRepository.findByEmail(singIn.getEmail())
			.orElseThrow(() -> new AuthenticationException(ErrorCode.EMAIL_NOT_FOUND));

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
		Member member = memberRepository.findByEmail(email)
			.orElseThrow(() -> new AuthenticationException(ErrorCode.MEMBER_NOT_FOUND));

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
		/** to-do 비밀번호 찾기
		member.setResetPasswordKey("");
		member.setResetPasswordLimitDt(null);
		 **/
		member.setZipcode("");
		member.setAddress1("");
		member.setAddress2("");
		memberRepository.save(member);

		return new ServiceResult(true, "탈퇴되었습니다.");
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Member member = memberRepository.findByEmail(username)
			.orElseThrow(() -> new UsernameNotFoundException("회원 정보가 없습니다."));

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
