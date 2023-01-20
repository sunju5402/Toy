package com.example.toyservice.model.entity;

import com.example.toyservice.model.constants.MemberStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
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
public class Member extends BaseEntity {
	private String email;
	private String name;
	private String nickname;
	private String password;
	private String zipcode;
	private String address1; // 지명주소
	private String address2; // 상세주소
	private String phone;
	private LocalDateTime regDt;
	private boolean admin;

	private boolean emailAuth;
	private LocalDateTime emailAuthDt;
	private String emailAuthKey;

	@Enumerated(EnumType.STRING)
	private MemberStatus status;
	private boolean rejoin;

	@OneToOne(mappedBy = "member", fetch = FetchType.LAZY, orphanRemoval = true)
	@JsonIgnore // 응답시, 순환참조 방지
	@JoinColumn(name = "wallet_id")
	private Wallet wallet;

	@OneToMany(mappedBy = "seller", cascade = CascadeType.ALL,
		fetch = FetchType.LAZY, orphanRemoval = true)
	@JsonIgnore
	private List<SellPost> sellPosts = new ArrayList<>();

	@OneToMany(mappedBy = "lender", cascade = CascadeType.ALL,
		fetch = FetchType.LAZY, orphanRemoval = true)
	@JsonIgnore
	private List<LendPost> lendPosts = new ArrayList<>();
}
