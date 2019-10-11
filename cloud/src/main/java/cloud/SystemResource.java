package cloud;


import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URISyntaxException;
import java.util.Enumeration;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;

@Path("/system")
public class SystemResource {

	@GET
	@Path("/login")
	@Produces(MediaType.TEXT_PLAIN)
	public String login2(@QueryParam("code") final String code, @QueryParam("name") final String name) {
		GoogleHandlerImpl googleHandler = CloudServer.googleHandler;
		
		String redirectUri = "http://" + getMyAddress();
		try {
			String access_token;

			if (googleHandler.hasStoredAccessToken(name)) {
				access_token = googleHandler.getStoredAccessToken(name);
			}
			else {
				GoogleAccressTokenResponse accessToken = googleHandler.retrieveAccessToken(code, redirectUri);
				access_token = accessToken.access_token;
			}
			GooglePersonalResponse personal = googleHandler.retrievePersonal(access_token);
			
			GoogleFile file = googleHandler.retrieveFiles(access_token);
			personal.access_token = access_token;
			
			return "LoggedIn;" + personal.family_name;//new KeyValue("Complete", personal.name);	
		}
		catch (Exception e) {
			e.printStackTrace();
			String url = googleHandler.getAuthUri(redirectUri);
			return "Redirect;" + url;
		}
	}
	
	private String getMyAddress() {
		return "localhost:8081";
		
//		Enumeration<NetworkInterface> ni;
//		try {
//		    ni = NetworkInterface.getNetworkInterfaces();
//		    while (ni.hasMoreElements()){
//		       NetworkInterface e = ni.nextElement();
//		        Enumeration<InetAddress> ai = e.getInetAddresses();
//		             while (ai.hasMoreElements()){
//		                 InetAddress address = ai.nextElement();
//		                 if(!"127.0.0.1".equals(address.getHostAddress()) && !address.getHostAddress().startsWith("0.0.0.0") && !address.getHostAddress().contains(":")){
//		                     return address.getHostAddress();
//		                 }
//		             }
//		    }
//		} catch (SocketException e1) {
//		    e1.printStackTrace();
//		}
//		return "localhost";
	}

}
