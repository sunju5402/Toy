package com.example.toyservice.service;

import com.example.toyservice.components.GpsComponents;
import com.example.toyservice.components.MailComponents;
import com.example.toyservice.dto.MemberDto;
import com.example.toyservice.dto.RevisionMember;
import com.example.toyservice.exception.AuthenticationException;
import com.example.toyservice.model.constants.Authority;
import com.example.toyservice.model.constants.ErrorCode;
import com.example.toyservice.model.constants.MemberStatus;
import com.example.toyservice.model.entity.Member;
import com.example.toyservice.model.entity.SellPost;
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
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class MemberService implements UserDetailsService {

	private final MemberRepository memberRepository;
	private final WalletService walletService;
	private final PasswordEncoder passwordEncoder;
	private final MailComponents mailComponents;
	private final GpsComponents gpsComponents;


	public Member register(String x, String y, MemberDto.Request member) {
		boolean existsByEmail = memberRepository.existsByEmail(member.getEmail());
		if (existsByEmail) {
			// 탈퇴한 회원인지 확인.
			if (!memberRepository.findByEmail(member.getEmail()).get().getNickname().equals("")) {
				throw new AuthenticationException(ErrorCode.MEMBER_ALREADY_EXIST);
			}
			// 로그인시 탈퇴회원과 구분하기 위해 email 수정.
			memberRepository.findByEmail(member.getEmail()).
				get().setEmail("탈퇴" + member.getEmail());
			member.setRejoin(true);
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
		Member saveMember = memberRepository.save(member.toEntity());
		walletService.add(saveMember);

		String email = member.getEmail();
		String subject = "ToyService 가입을 축하드립니다. ";
		String text = "<p>ToyService 가입을 축하드립니다."
			+ "<p><p>아래 링크를 클릭하셔서 가입을 완료 하세요.</p>"
			+ "<div><a target='_blank' href='http://localhost:8080/email-auth?id=" + uuid + "'> 가입 완료 </a></div>";
		mailComponents.sendMail(email, subject, text);

		return member.toEntity();
	}

	public Member getMember(Long id) {
		return memberRepository.findById(id)
			.orElseThrow(() -> new AuthenticationException(ErrorCode.MEMBER_NOT_FOUND));
	}

	public RevisionMember.Response revise(Long memberId, RevisionMember.Request revisionMember) {
		Member member = getMember(memberId);

		boolean existsByNickname = memberRepository.existsByNickname(revisionMember.getNickname());
		if (existsByNickname) {
			throw new AuthenticationException(ErrorCode.NICKNAME_ALREADY_EXIST);
		}

		member.setNickname(revisionMember.getNickname());
		member.setPassword(passwordEncoder.encode(revisionMember.getPassword()));
		member.setPhone(revisionMember.getPhone());

		return RevisionMember.Response.fromEntity(memberRepository.save(member));
	}

	public MemberDto.Response emailAuth(String emailAuthKey) {
		Member member = memberRepository.findByEmailAuthKey(emailAuthKey)
			.orElseThrow(() -> new AuthenticationException(ErrorCode.EMAILAUTHKEY_NOT_FOUND));

		if (member.isEmailAuth()) {
			throw new AuthenticationException(ErrorCode.EMAIL_ALREADY_ACTIVATE);
		}

		member.setStatus(MemberStatus.ING);
		member.setEmailAuth(true);
		member.setEmailAuthDt(LocalDateTime.now());
		return MemberDto.Response.fromEntity(memberRepository.save(member));
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

	public MemberDto.Withdraw withdraw(Long id, MemberDto.SignIn request) {
		Member member = getMember(id);

		if (member.getEmail().contains("탈퇴")) {
			throw new AuthenticationException(ErrorCode.MEMBER_WITHDRAW);
		}

		if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
			throw new AuthenticationException(ErrorCode.PASSWORD_NOT_MATCH);
		}

		member.setName("사용자 정보가 없습니다.");
		member.setPhone("");
		member.setPassword("");
		member.setNickname("");
		member.setRegDt(null);
		member.setEmailAuth(false);
		member.setEmailAuthDt(null);
		member.setEmailAuthKey("");
		member.setStatus(MemberStatus.WITHDRAW);
		member.setZipcode("");
		member.setAddress1("");
		member.setAddress2("");
		member.setWallet(null);
		member.getSellPosts().clear();
		member.getSellPosts().stream().map(x -> null);

		return MemberDto.Withdraw.fromEntity(memberRepository.save(member));
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
