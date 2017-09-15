package com.ibm.creation;
 
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.cert.X509Certificate;
import java.util.Properties;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Create_local extends HttpServlet { 
	 private static final long serialVersionUID = 1L;
     
	    public Create_local() {
	        // TODO Auto-generated constructor stub
	    }  

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		
		String campaign_ID="";
		String user = request.getParameter("user");
		String token = request.getParameter("token");
		String template_ID = request.getParameter("template_ID");
		String campaign_name = request.getParameter("campaign_name");
		String description = request.getParameter("description");
		String password = request.getParameter("password");
		
		Properties prop = new Properties();
		  prop.load(getServletContext().getResourceAsStream("/WEB-INF/config.properties"));
		  System.out.println(prop.getProperty("url_DM"));
		  String url_DM = prop.getProperty("url_DM");
		  String protocol = prop.getProperty("protocol");
		  String port = prop.getProperty("port");
		
		MediaType mediaType = MediaType.parse("application/json");
		
		RequestBody body = RequestBody.create(mediaType, "{\r\n\"Project_Name\": \""+campaign_name+"\",\r\n\"Template_DB_ID\": \""+template_ID+"\",\r\n\"Security_Policy\": \"Global\",\r\n\"Description\": \""+description+"\"\r\n} ");
		
		System.out.println("Request : " + "{\r\n\"Project_Name\": \""+campaign_name+"\",\r\n\"Template_DB_ID\": \""+template_ID+"\",\r\n\"Security_Policy\": \"Global\",\r\n\"Description\": \""+description+"\"\r\n} ");
		
		// Create a trust manager that does not validate certificate chains
		  TrustManager[] trustAllCerts = new TrustManager[] { 
		      new X509TrustManager() {     
		          public java.security.cert.X509Certificate[] getAcceptedIssuers() { 
		              return new X509Certificate[0];
		          } 
		          public void checkClientTrusted( 
		              java.security.cert.X509Certificate[] certs, String authType) {
		              } 
		          public void checkServerTrusted( 
		              java.security.cert.X509Certificate[] certs, String authType) {
		          }
		      } 
		  }; 

		  // Install the all-trusting trust manager
		  try {
		      SSLContext  sc= SSLContext.getInstance("SSL"); 
		      sc.init(null, trustAllCerts, new java.security.SecureRandom()); 
		      
		      
		      OkHttpClient client = new OkHttpClient.Builder()
		        .sslSocketFactory(sc.getSocketFactory())
		         .hostnameVerifier(new HostnameVerifier() {
	                    @Override
	                    public boolean verify(String hostname, SSLSession session) {
	                        return true;
	                    }
	                })
		        .build();
		      
		Request request_call = new Request.Builder()
		  .url(protocol+"://"+url_DM+":"+port+"/collaborate/api/collaborate/v1/locals")
		  .post(body)
		  .addHeader("m_user_name", user)
		  .addHeader("m_tokenid", token)
		  .addHeader("authorization", "application/json")
		  .addHeader("api_auth_mode", "manager")
		  .addHeader("content-type", "application/json")
		  .addHeader("cache-control", "no-cache")
		  .build();

		Response response_call = client.newCall(request_call).execute();
		int status = response_call.code();
		System.out.println("STATUS " + status);
		
		
		 
		/* Rest Call for Bluemix
		  RestCall call = new RestCall();
		  String[] retour = call.PostCall_Location("http://"+url_DM+":7001/collaborate/api/collaborate/v1/locals","{\r\n\"Project_Name\": \""+campaign_name+"\",\r\n\"Template_DB_ID\": \""+template_ID+"\",\r\n\"Security_Policy\": \"Global\",\r\n\"Description\": \""+description+"\"\r\n} ","application/json", user, token);
		  // End of Rest Call*/
		
		
		if (status==201){
			String LOCATION = response_call.headers().get("Location");
			campaign_ID = LOCATION.split("locals/")[1];
			System.out.println("campaign_ID " + campaign_ID);
			response.sendRedirect("app.jsp?user="+user+"&password="+password+"&campaign_ID="+campaign_ID);
		}	
		
		
		response.setContentType("text/plain");  
		response.setCharacterEncoding("UTF-8"); 
		response.getWriter().write(campaign_ID);
	 } catch (GeneralSecurityException e) {
	  } 
	}
	
	 protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		  // TODO Auto-generated method stub
		  
		 }
	
}
