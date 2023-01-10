package com.example.toyservice.model.entity;

import com.example.toyservice.model.constants.MemberStatus;
import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
	private boolean isAdmin;

	private boolean emailAuthYn;
	private LocalDateTime emailAuthDt;
	private String emailAuthKey;

	@Enumerated(EnumType.STRING)
	private MemberStatus status;

}
