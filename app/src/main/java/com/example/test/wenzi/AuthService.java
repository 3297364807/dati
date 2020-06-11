package com.example.test.wenzi;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

/**
 * 
 * @author csh
 *
 *         获取token�?
 */
public class AuthService {

	 /**
	  * 获取权限token
     * @return 返回token
     */
	
	public  String getAuth() {
		String clientId = "N2AwQMQA1A7vStNefh54003L";
		String clientSecret = "5ExplzDpeKs3AbXM8fkSbEGGybtx5Mab";
		return getAuth(clientId, clientSecret);
	}
	public static String getAuth(String ak, String sk) {
		// 获取token地址
		String authHost = "https://aip.baidubce.com/oauth/2.0/token?";
		String getAccessTokenUrl = authHost
				+ "grant_type=client_credentials"
				+ "&client_id=" + ak
				+ "&client_secret=" + sk;
		try {
			URL realUrl = new URL(getAccessTokenUrl);
			HttpURLConnection connection = (HttpURLConnection) realUrl.openConnection();
			connection.setRequestMethod("GET");
			connection.connect();
			Map<String, List<String>> map = connection.getHeaderFields();
			for (String key : map.keySet()) {
				System.err.println(key + "--->" + map.get(key));
			}
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String result = "";
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
			System.err.println("result:" + result);
			JSONObject jsonObject = new JSONObject(result);
			String access_token = jsonObject.getString("access_token");
			return access_token;
		} catch (Exception e) {
			System.err.printf("获取token失败�?");
			e.printStackTrace(System.err);
		}
		return null;
	}
}
