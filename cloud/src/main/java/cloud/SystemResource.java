package cloud;


import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.commons.codec.binary.Base64;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

@Path("/system")
public class SystemResource {
	private static GoogleHandlerImpl googleHandler = CloudServer.googleHandler;
//    @POST
//    @Path("/upload")
//    @Consumes(MediaType.MULTIPART_FORM_DATA)
//    public Response uploadFile(
//        @FormDataParam("file") InputStream uploadedInputStream,
//        @FormDataParam("file") FormDataContentDisposition fileDetail) {
//
//        String uploadedFileLocation = fileDetail.getFileName();
//
//       //save it
//        writeToFile(uploadedInputStream, uploadedFileLocation);
//
//        String output = "File uploaded to : " + uploadedFileLocation;
//
//        return Response.status(200).entity(output).build();
//
//    }
//
//   //save uploaded file to new location
//    private void writeToFile(InputStream uploadedInputStream,
//        String uploadedFileLocation) {
//
//        try {
//            OutputStream out = new FileOutputStream(new File(
//                    uploadedFileLocation));
//            int read = 0;
//            byte[]bytes = new byte[1024];
//
//            out = new FileOutputStream(new File(uploadedFileLocation));
//            while ((read = uploadedInputStream.read(bytes)) != -1) {
//                out.write(bytes, 0, read);
//            }
//            out.flush();
//            out.close();
//        } catch (IOException e) {
//
//            e.printStackTrace();
//        }
//
//    }
	@POST
	@Path("/post")
	@Produces(MediaType.TEXT_PLAIN)
	public String post(String base64) {
		String[] tmp = base64.split(",");
		
		String type = tmp[0].split("[:;]+")[1];
		String data = tmp[1];
//		byte[] b = Base64.decodeBase64(base64.split(",")[1]);//data.replace("data:application/octet-stream;base64,", ""));

		try {
			DataOutputStream dataOutStream = 
			        new DataOutputStream(
			          new BufferedOutputStream(
			            new FileOutputStream("tmpfile.gif")));
			dataOutStream.write(Base64.decodeBase64(data));
			dataOutStream.flush();
			dataOutStream.close();
			
			googleHandler.post(type, new File("tmpfile.gif"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return "OK";
	}
	
	@GET
	@Path("/login")
	@Produces(MediaType.TEXT_PLAIN)
	public String login2(@QueryParam("code") final String code, @QueryParam("name") final String name,
			 @QueryParam("clientId") final String clientId,  @QueryParam("clientSecret") final String clientSecret) {
				
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
			googleHandler.setAccesstoken(access_token);
			GooglePersonalResponse personal = googleHandler.retrievePersonal();
			
			GoogleFile file = googleHandler.retrieveFiles();
			System.out.println(file);
			
			
			personal.access_token = access_token;
			
			return "LoggedIn;" + personal.family_name;//new KeyValue("Complete", personal.name);	
		}
		catch (Exception e) {
		//	e.printStackTrace();
			googleHandler.setClientIdSecret(clientId, clientSecret);
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
