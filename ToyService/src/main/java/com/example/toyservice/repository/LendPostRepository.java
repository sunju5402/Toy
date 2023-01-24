package com.example.toyservice.repository;

import com.example.toyservice.model.constants.BorrowStatus;
import com.example.toyservice.model.entity.LendPost;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LendPostRepository extends JpaRepository<LendPost, Long> {
	List<LendPost> findAllByLenderId(Long id, Pageable pageable);
	List<LendPost> findAllByBorrowStatus(BorrowStatus status);
}
