package com.example.toyservice.components;

import com.example.toyservice.exception.ApiRequestException;
import com.example.toyservice.model.constants.ErrorCode;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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
		String addr = "";

		try {
			addr = getZipcodeAndDongName(getJSONData(url));
		} catch (ApiRequestException e) {
			throw  new ApiRequestException(ErrorCode.API_REQUEST_FAIL);
		} catch (Exception e) {
			System.out.println("주소 api 요청 에러");
			e.printStackTrace();
		}

		return addr;
	}

	/**
	 * REST API로 통신하여 받은 JSON형태의 데이터를 String으로 받아오는 메소드
	 */
	private String getJSONData(String apiUrl) throws Exception {
		HttpURLConnection conn = null;
		StringBuffer response = new StringBuffer();

		String auth = "KakaoAK " + apiKey;

		//URL 설정
		URL url = new URL(apiUrl);

		conn = (HttpURLConnection) url.openConnection();

		//Request 형식 설정
		conn.setRequestMethod("GET");
		conn.setRequestProperty("X-Requested-With", "curl");
		conn.setRequestProperty("Authorization", auth);

		//request에 JSON data 준비
		conn.setDoOutput(true);

		//보내고 결과값 받기
		int responseCode = conn.getResponseCode();
		if (responseCode == 400) {
			System.out.println("400:: 해당 명령을 실행할 수 없음");
		} else if (responseCode == 401) {
			System.out.println("401:: Authorization가 잘못됨");
		} else if (responseCode == 500) {
			System.out.println("500:: 서버 에러, 문의 필요");
		} else { // 성공 후 응답 JSON 데이터받기
			Charset charset = Charset.forName("UTF-8");
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), charset));

			String inputLine;
			while ((inputLine = br.readLine()) != null) {
				response.append(inputLine);
			}
		}

		return response.toString();
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
