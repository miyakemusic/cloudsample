package cloud;


import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GoogleHandlerImpl {
	
	private Map<String, String> acccessTokenMap = new HashMap<>();
	
	
//	private String client_id = "";
//	private String client_secret = "";
	
	public GoogleAccressTokenResponse retrieveAccessToken(String code, String redirectUri, String client_id, String client_secret) throws Exception {		
        Map<String, String> formParams = new HashMap<>();
      
        formParams.put("client_id", client_id);
        formParams.put("client_secret", client_secret);
        formParams.put("redirect_uri", redirectUri);
        
        formParams.put("grant_type", "authorization_code");
        formParams.put("access_type", "offline");
        formParams.put("code", code);
        
        System.out.println("client_id=" + client_id);
        System.out.println("client_secret=" + client_secret);
        System.out.println("code=" + code);
        
        final FormBody.Builder formBuilder = new FormBody.Builder();
        formParams.forEach((k, v) -> formBuilder.add(k, v));
        RequestBody requestBody = formBuilder.build();
        System.out.println("Request request = new Request.Builder()");
        Request request = new Request.Builder()
                .url("https://www.googleapis.com/oauth2/v4/token")
                .post(requestBody)
                .build();

        OkHttpClient client = new OkHttpClient();

        System.out.println("OkHttpClient");
        Response responseOk = client.newCall(request).execute();
        String json = responseOk.body().string();   
        
        System.out.println("Object Mapper");
        GoogleAccressTokenResponse response = new ObjectMapper().readValue(json, GoogleAccressTokenResponse.class);
        System.out.println("access token=" + response.access_token);
        return response;
	}

	public GooglePersonalResponse retrievePersonal(String accessToken) throws Exception {
		OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://www.googleapis.com/oauth2/v1/userinfo?alt=json&access_token=" + accessToken)
                .get()
                .build();
        Response responseOk = client.newCall(request).execute();
		return new ObjectMapper().readValue(responseOk.body().string(), GooglePersonalResponse.class);	
	}

	public String getAuthUri(String redirectUri, String client_id) {
		return "https://accounts.google.com/o/oauth2/auth?response_type=code&client_id=" + client_id + "&redirect_uri=" + redirectUri
				+ "&scope=https://www.googleapis.com/auth/userinfo.profile&access_type=offline&approval_prompt=force";
	}

	public boolean hasStoredAccessToken(String name) {
		return acccessTokenMap.containsKey(name);
	}

	public String getStoredAccessToken(String name) {
		return acccessTokenMap.get(name);
	}


}
