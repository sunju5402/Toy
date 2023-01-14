package com.example.toyservice.repository;

import com.example.toyservice.model.entity.Member;
import com.example.toyservice.model.entity.Wallet;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {
	Optional<Wallet> findByMemberId(Long id);
}
