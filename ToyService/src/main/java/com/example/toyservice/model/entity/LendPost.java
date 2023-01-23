package com.example.toyservice.model.entity;

import com.example.toyservice.model.constants.BorrowStatus;
import com.example.toyservice.model.constants.LendStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
@ToString
@Entity
public class LendPost extends BaseEntity {
	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnore
	@JoinColumn(name = "lender_id")
	private Member lender;

	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnore
	@JoinColumn(name = "borrower_id")
	private Member borrower;

	private String title;
	private String toyName;
	private String image;
	private String content;
	private int lendPeriod;

	private LocalDateTime borrowAt;
	private int borrowPeriod;
	@Enumerated(EnumType.STRING)
	private BorrowStatus borrowStatus;

	private int overduePeriod;

	@Enumerated(EnumType.STRING)
	private LendStatus status;
}
