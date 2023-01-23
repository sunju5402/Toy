package com.example.toyservice;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.toyservice.model.constants.MemberStatus;
import com.example.toyservice.model.entity.Member;
import com.example.toyservice.repository.MemberRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class JpaMemberRepositoryTest {

	@Autowired
	MemberRepository memberRepository;

	@Test
	void insert() {
	    // given
		Member m = Member.builder()
			.email("sunju5402@naver.com")
			.name("name")
			.nickname("nickname")
			.password("password")
			.zipcode("zipcode")
			.address1("address1")
			.address2("address2")
			.phone("phone")
			.admin(true)
			.status(MemberStatus.REQ)
			.regDt(LocalDateTime.now())
			.emailAuth(false)
			.build();
	    // when
		memberRepository.save(m);
		List<Member> list = memberRepository.findAll();
		// then
		assertTrue(list.size() > 0);
	}
}
