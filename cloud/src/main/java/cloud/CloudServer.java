package cloud;


import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.webapp.WebAppContext;

public class CloudServer {
	
//	public static String clientId;
//	public static String clientSecret;
	public static GoogleHandlerImpl googleHandler = new GoogleHandlerImpl();
	
	public static void main(String[] arg) {
		new CloudServer(8081, arg[0], arg[1]);
	}

	
	public CloudServer(final int port, String clientId, String clientSecret) {
		
		new Thread() {
			@Override
			public void run() {
				initializeWebServer(port, "", "");
			}
		}.start();
	}
	
	private void initializeWebServer(int port, String clientId, String clientSecret) {
//		googleHandler.setClientIdSecret(clientId, clientSecret);
//		googleHandler.setProxyInfo(user, password);
		
		Server server = null;//new Server(port);
        	
		server = new Server(port);
		String xml = this.getClass().getPackage().getName().replace(".", "/") + "/web.xml";
		String resource = this.getClass().getPackage().getName().replace(".", "/");
        String xmlPath = "";
        String resourcePath = "";
                
        try {
	        xmlPath = this.getClass().
	     	       getClassLoader().getResource(xml).toExternalForm();
	        resourcePath = this.getClass().
	  	       getClassLoader().getResource(resource).toExternalForm();
        }
        catch (Exception e) {
        	e.printStackTrace();
        	xmlPath = xml;
        	resourcePath = resource;
        }
         
        HandlerCollection handlers = new HandlerCollection();
                
        // Jersey Servlet
        WebAppContext webAppContext = new WebAppContext();
        webAppContext.setDescriptor(xmlPath);   
        webAppContext.setResourceBase(resourcePath);
        webAppContext.setServer(server);
        webAppContext.setContextPath("/");
        handlers.addHandler(webAppContext);  
        
        server.setHandler(handlers);

        try {
            server.start();
            server.join();
            while (System.in.available() == 0) {
                Thread.sleep(5000);
            }
            server.stop();
            
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(100);
        }
	}
}
