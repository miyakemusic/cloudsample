package cloud;


import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.Authenticator;
import okhttp3.Credentials;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.Route;

public class GoogleHandlerImpl {
	
	private Map<String, String> acccessTokenMap = new HashMap<>();
	private String clientId;
	private String clientSecret;
	
	
//	private String client_id = "";
//	private String client_secret = "";
	
	public GoogleAccressTokenResponse retrieveAccessToken(String code, String redirectUri) throws Exception {		
        Map<String, String> formParams = new HashMap<>();
      
        formParams.put("client_id", clientId);
        formParams.put("client_secret", clientSecret);
        formParams.put("redirect_uri", redirectUri);
        
        formParams.put("grant_type", "authorization_code");
        formParams.put("access_type", "offline");
        formParams.put("code", code);
        
        final FormBody.Builder formBuilder = new FormBody.Builder();
        formParams.forEach((k, v) -> formBuilder.add(k, v));
        RequestBody requestBody = formBuilder.build();
        System.out.println("Request request = new Request.Builder()");
        Request request = new Request.Builder()
                .url("https://www.googleapis.com/oauth2/v4/token")
                .post(requestBody)
                .build();

        OkHttpClient client = createHttpClient();//new OkHttpClient();

        System.out.println("OkHttpClient");
        Response responseOk = client.newCall(request).execute();
        String json = responseOk.body().string();   
        
        System.out.println("Object Mapper");
        GoogleAccressTokenResponse response = new ObjectMapper().readValue(json, GoogleAccressTokenResponse.class);
        System.out.println("access token=" + response.access_token);
        return response;
	}

	public GooglePersonalResponse retrievePersonal(String accessToken) throws Exception {
		OkHttpClient client = createHttpClient();//new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://www.googleapis.com/oauth2/v1/userinfo?alt=json&access_token=" + accessToken)
                .get()
                .build();
        Response responseOk = client.newCall(request).execute();
		return new ObjectMapper().readValue(responseOk.body().string(), GooglePersonalResponse.class);	
	}

	public GoogleFile retrieveFiles(String accessToken) throws Exception {
		OkHttpClient client = createHttpClient();//new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://www.googleapis.com/drive/v3/files?access_token=" + accessToken)
                .get()
                .build();
        Response responseOk = client.newCall(request).execute();
        GoogleFile ret = new ObjectMapper().readValue(responseOk.body().string(), GoogleFile.class);
        return ret;
	}
		
	public String postFile(String accessToken) {       
		File image = new File("C:\\Users\\a1199022\\Pictures\\ticci-ipad-portrait_ipadmini_black_landscape.jpg");
		
        final RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpg"), image);    
		
//		RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
//                .addFormDataPart("file", "myfile.jpg", RequestBody.create(MediaType.parse("image/jpg"), image))
//                .build();
		
		OkHttpClient client = createHttpClient();//new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://www.googleapis.com/upload/drive/v3/files?uploadType=media")//&access_token=" + accessToken)
                .addHeader("Authorization", "Bearer " + accessToken)
                .addHeader("Content-Type", "image/jpeg")
                .addHeader("Content-Length", String.valueOf(image.length()))
                .post(requestBody)
                .build();
        try {
			Response responseOk = client.newCall(request).execute();
			return responseOk.toString();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
        return "";
	}
	
	public String getAuthUri(String redirectUri) {
		return "https://accounts.google.com/o/oauth2/auth?response_type=code&client_id=" + clientId + "&redirect_uri=" + redirectUri
				+ "&scope=https://www.googleapis.com/auth/userinfo.profile" + 
				"%20https://www.googleapis.com/auth/drive.appdata" + 
				"%20https://www.googleapis.com/auth/drive.file" + 
				"&access_type=offline&approval_prompt=force";
	}

	public boolean hasStoredAccessToken(String name) {
		return acccessTokenMap.containsKey(name);
	}

	public String getStoredAccessToken(String name) {
		return acccessTokenMap.get(name);
	}

	public void setClientIdSecret(String clientId, String clientSecret) {
		this.clientId = clientId;
		this.clientSecret = clientSecret;
	}

	private OkHttpClient createHttpClient() {
		OkHttpClient client = new OkHttpClient();
		return client;		
	}
	
//	private OkHttpClient createHttpClientProxy() {
//        Authenticator proxyAuthenticator = new Authenticator() {
//            @Override
//            public Request authenticate(Route route,
//                    Response response) throws IOException {
//                String credential = Credentials.basic(user, password);
//                return response.request().newBuilder()
//                        .header("Proxy-Authorization", credential).build();
//            }
//        };
//		Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("http-proxy-atsugi.jp.anritsu.com", 8080));
//		OkHttpClient client = new OkHttpClient.Builder()
//		                          .proxy(proxy)
//		                          .proxyAuthenticator(proxyAuthenticator)
//		                          .build();
//		return client;
//	}
}
