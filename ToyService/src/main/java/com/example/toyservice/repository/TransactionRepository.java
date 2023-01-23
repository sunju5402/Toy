package com.example.toyservice.repository;

import com.example.toyservice.model.entity.Transaction;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
	List<Transaction> findAllByWalletId(Long id);
}
