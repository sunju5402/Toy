package com.example.toyservice;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.toyservice.repository.MemberRepository;
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
//		Member m = new Member(1, "2", "3");
//	    // when
//		memberRepository.save(m);
//	    // then
//
//		List<Member> list = memberRepository.findAll();
//
//		assertTrue(list.size() > 0);
	}
}
