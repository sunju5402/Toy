package com.example.toyservice.components;

import com.example.toyservice.dto.KakaoApi;
import com.example.toyservice.exception.ApiRequestException;
import com.example.toyservice.model.constants.ErrorCode;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
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
		String url = "https://dapi.kakao.com/v2/local/geo/coord2address.json?x=" + x + "&y=" + y;

		try {
			return getZipcodeAndDongName(getJSONData(url));
		} catch (Exception e) {
			throw new ApiRequestException(ErrorCode.API_REQUEST_FAIL);
		}
	}

	/**
	 * REST API로 통신하여 받은 JSON형태의 데이터를 KakaoApi로 받아오는 메소드
	 */
	private KakaoApi getJSONData(String apiUrl) {
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

		ResponseEntity<KakaoApi> result = restTemplate.exchange(targetUrl,
			HttpMethod.GET, httpEntity, KakaoApi.class);

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

	private String getZipcodeAndDongName(KakaoApi kakaoApi) { // 주소에서 우편번호와 "동"이름 가져오기
		int size = kakaoApi.getDocuments().size();
		if (size == 0) {
			throw new ApiRequestException(ErrorCode.NOT_EXIST_ADDRESS);
		}

		boolean existZoneNo = false;
		boolean existDong = false;
		String zoneNo = "";
		String dong = "";
		for (int i = 0; i < size; i++) {
			zoneNo = kakaoApi.getDocuments().get(i).getRoad_address().getZone_no();
			dong = kakaoApi.getDocuments().get(i).getAddress().getRegion_3depth_name();
			if (!zoneNo.equals("") && zoneNo != null) {
				existZoneNo = true;
			}
			if (!dong.equals("") && dong != null) {
				existDong = true;
			}

			if (existZoneNo && existDong) {
				return zoneNo + " " + dong;
			}
			existZoneNo = false;
			existDong = false;
		}

		throw new ApiRequestException(ErrorCode.NOT_EXIST_ADDRESS);
	}
}
