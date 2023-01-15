package com.example.toyservice.model.entity;

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
public class SellPost extends BaseEntity{
	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnore
	@JoinColumn(name = "seller_id")
	private Member seller;

	private String title;
	private String name;
	private int price;
	private String image;
	private String content;

	@Enumerated(EnumType.STRING)
	private SellStatus status;
}