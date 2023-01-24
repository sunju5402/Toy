package com.example.toyservice.repository;

import com.example.toyservice.model.entity.SellPost;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SellPostRepository extends JpaRepository<SellPost, Long> {
	List<SellPost> findAllBySellerId(Long id, Pageable pageable);
}
