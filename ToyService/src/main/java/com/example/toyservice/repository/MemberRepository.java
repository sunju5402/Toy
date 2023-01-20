package com.example.toyservice.repository;

import com.example.toyservice.model.constants.MemberStatus;
import com.example.toyservice.model.entity.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
	Optional<Member> findByEmailAuthKey(String emailAuthKey);
	Optional<Member> findByEmail(String email);
	Optional<Member> findByEmailAndStatusNot(String email, MemberStatus status);
	boolean existsByEmail(String email);
	boolean existsByNickname(String nickname);
}
