package com.example.toyservice.components;

import com.example.toyservice.exception.ApiRequestException;
import com.example.toyservice.model.constants.ErrorCode;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@RequiredArgsConstructor
@Component
public class GpsComponents {

	@Value("${kakao.key}")
	String apiKey;

	/**
	 * 위도와 경도를 이용하여 카카오주소api를 통해 주소 변환
	 */
	public String latAndLonToAddr(String x, String y) {
		String url = "https://dapi.kakao.com/v2/local/geo/coord2address.json?x="+x+"&y="+y;

		try {
			return getZipcodeAndDongName(getJSONData(url));
		} catch (Exception e) {
			throw  new ApiRequestException(ErrorCode.API_REQUEST_FAIL);
		}
	}

	/**
	 * REST API로 통신하여 받은 JSON형태의 데이터를 String으로 받아오는 메소드
	 */
	private String getJSONData(String apiUrl) {
		String auth = "KakaoAK " + apiKey;

		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add("Authorization", auth);
		httpHeaders.add("X-Requested-With", "curl");
		HttpEntity<String> httpEntity = new HttpEntity<>(httpHeaders);

		URI targetUrl = UriComponentsBuilder
			.fromUriString(apiUrl)
			.build()
			.encode(StandardCharsets.UTF_8) //인코딩
			.toUri();

		ResponseEntity<String> result = restTemplate.exchange(targetUrl,
			HttpMethod.GET, httpEntity, String.class);

		int responseCode = result.getStatusCodeValue();
		if (responseCode == 400) {
			throw new ApiRequestException(ErrorCode.ERROR_CODE_400);
		} else if (responseCode == 401) {
			throw new ApiRequestException(ErrorCode.ERROR_CODE_401);
		} else if (responseCode == 500) {
			throw new ApiRequestException(ErrorCode.ERROR_CODE_500);
		}

		return result.getBody();
	}

	private String getZipcodeAndDongName(String jsonString) { // 주소에서 우편번호와 "동"이름 가져오기
		String result = "";
		JSONObject jObj = (JSONObject) JSONValue.parse(jsonString);
		JSONObject meta = (JSONObject) jObj.get("meta");
		long size = (long) meta.get("total_count");

		if(size > 0){
			JSONArray jArray = (JSONArray) jObj.get("documents");
			JSONObject subJob = (JSONObject) jArray.get(0);

			JSONObject roadAddress = (JSONObject) subJob.get("road_address");
			result = (String) roadAddress.get("zone_no") + " ";

			JSONObject address = (JSONObject) subJob.get("address");
			result += (String) address.get("region_3depth_name"); // 동이름

			if(result.equals("") || result==null){
				subJob = (JSONObject) jArray.get(1);
				subJob = (JSONObject) subJob.get("address");
				result =(String) subJob.get("region_3depth_name");
			}
		}

		return result;
	}
}
