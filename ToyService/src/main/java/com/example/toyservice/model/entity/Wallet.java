package com.example.toyservice.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
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
public class Wallet extends BaseEntity {
	@OneToOne(fetch = FetchType.LAZY)
	@JsonIgnore // 응답시, 순환참조 방지
	@JoinColumn(name = "member_id")
	private Member member;
	private Long balance;
}
