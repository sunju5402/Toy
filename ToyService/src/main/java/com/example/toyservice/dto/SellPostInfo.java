package com.example.toyservice.dto;

import com.example.toyservice.model.constants.SellStatus;
import com.example.toyservice.model.entity.SellPost;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SellPostInfo {
	private long id;
	private String sellerEmail;
	private String title;
	private String toyName;
	private String image;
	private long price;
	private String content;;
	private SellStatus status;

	public static List<SellPostInfo> of(List<SellPost> sellPosts) {
		if (sellPosts != null) {
			List<SellPostInfo> postList = new ArrayList<>();
			for (SellPost x : sellPosts) {
				postList.add(fromEntity(x));
			}
			return postList;
		}
		return null;
	}

	public static SellPostInfo fromEntity(SellPost sellPost) {
		return SellPostInfo.builder()
			.id(sellPost.getId())
			.sellerEmail(sellPost.getSeller().getEmail())
			.title(sellPost.getTitle())
			.toyName(sellPost.getToyName())
			.image(sellPost.getImage())
			.price(sellPost.getPrice())
			.content(sellPost.getContent())
			.status(sellPost.getStatus())
			.build();
	}
}
