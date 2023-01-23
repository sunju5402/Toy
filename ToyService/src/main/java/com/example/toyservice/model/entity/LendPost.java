package com.example.toyservice.model.entity;

import com.example.toyservice.model.constants.LendStatus;
import com.example.toyservice.model.constants.SellStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
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

	private String title;
	private String toyName;
	private String image;
	private String content;
	private int lendPeriod;

	@Enumerated(EnumType.STRING)
	private LendStatus status;
}
