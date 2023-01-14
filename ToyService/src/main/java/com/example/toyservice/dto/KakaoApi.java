package com.example.toyservice.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class KakaoApi {
	private Meta meta;
	private List<Documents> documents;

	@Setter
	@Getter
	@AllArgsConstructor
	@NoArgsConstructor
	public static class Meta {
		private long total_count;
	}

	@Setter
	@Getter
	@AllArgsConstructor
	@NoArgsConstructor
	public static class Documents {
		private RoadAddress road_address;
		private Address address;
	}

	@Setter
	@Getter
	@AllArgsConstructor
	@NoArgsConstructor
	public static class RoadAddress {
		private String address_name;
		private String region_1depth_name;
		private String region_2depth_name;
		private String region_3depth_name;
		private String road_name;
		private String underground_yn;
		private String main_building_no;
		private String sub_building_no;
		private String building_name;
		private String zone_no;
	}

	@Setter
	@Getter
	@AllArgsConstructor
	@NoArgsConstructor
	public static class Address {
		private String address_name;
		private String region_1depth_name;
		private String region_2depth_name;
		private String region_3depth_name;
		private String mountain_yn;
		private String main_address_no;
		private String sub_address_no;
		private String zip_code;
	}
}
