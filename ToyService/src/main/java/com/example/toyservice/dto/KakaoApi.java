package com.example.toyservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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
		@JsonProperty("total_count")
		private long totalCount;
	}

	@Setter
	@Getter
	@AllArgsConstructor
	@NoArgsConstructor
	public static class Documents {
		@JsonProperty("road_address")
		private RoadAddress roadAddress;
		private Address address;
	}

	@Setter
	@Getter
	@AllArgsConstructor
	@NoArgsConstructor
	public static class RoadAddress {
		@JsonProperty("address_name")
		private String addressName;
		@JsonProperty("region_1depth_name")
		private String region1DepthName;
		@JsonProperty("region_2depth_name")
		private String region2DepthName;
		@JsonProperty("region_3depth_name")
		private String region3DepthName;
		@JsonProperty("road_name")
		private String roadName;
		@JsonProperty("underground_yn")
		private String undergroundYn;
		@JsonProperty("main_building_no")
		private String mainBuildingNo;
		@JsonProperty("sub_building_no")
		private String subBuildingNo;
		@JsonProperty("building_name")
		private String buildingName;
		@JsonProperty("zone_no")
		private String zoneNo;
	}

	@Setter
	@Getter
	@AllArgsConstructor
	@NoArgsConstructor
	public static class Address {
		@JsonProperty("address_name")
		private String addressName;
		@JsonProperty("region_1depth_name")
		private String region1DepthName;
		@JsonProperty("region_2depth_name")
		private String region2DepthName;
		@JsonProperty("region_3depth_name")
		private String region3DepthName;
		@JsonProperty("mountain_yn")
		private String mountainYn;
		@JsonProperty("main_address_no")
		private String mainAddressNo;
		@JsonProperty("sub_address_no")
		private String subAddressNo;
		@JsonProperty("zip_code")
		private String zipCode;
	}
}
