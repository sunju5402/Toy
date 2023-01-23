package com.example.toyservice.dto;

import com.example.toyservice.model.constants.LendStatus;
import com.example.toyservice.model.entity.LendPost;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.CollectionUtils;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LendPostInfo {
	private long id;
	private String lenderEmail;
	private String title;
	private String toyName;
	private String image;
	private int period;
	private String content;;
	private LendStatus status;

	public static List<LendPostInfo> of(List<LendPost> lendPosts) {
		if (CollectionUtils.isEmpty(lendPosts)) {
			return null;
		}
		return lendPosts.stream()
			.map(LendPostInfo::fromEntity)
			.collect(Collectors.toList());
	}

	public static LendPostInfo fromEntity(LendPost lendPost) {
		return LendPostInfo.builder()
			.id(lendPost.getId())
			.lenderEmail(lendPost.getLender().getEmail())
			.title(lendPost.getTitle())
			.toyName(lendPost.getToyName())
			.image(lendPost.getImage())
			.period(lendPost.getLendPeriod())
			.content(lendPost.getContent())
			.status(lendPost.getStatus())
			.build();
	}
}
