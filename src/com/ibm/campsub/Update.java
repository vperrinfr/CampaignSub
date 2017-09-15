package com.ibm.campsub;

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

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class Update extends HttpServlet { 
	 private static final long serialVersionUID = 1L;
    
	    public Update() {
	        // TODO Auto-generated constructor stub
	    }  

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String user = request.getParameter("user");
		String token = request.getParameter("token");
		String pdv = request.getParameter("campaign_ID");
		
		String PDV_Attribute_Name = request.getParameter("PDV_Attribute_Name");
		String Project_ID = request.getParameter("Project_ID");
		String PDV_Attribute_ID = request.getParameter("PDV_Attribute_ID");
		String PDV_Attribute_Values = request.getParameter("PDV_Attribute_Values");
		String Subscription = request.getParameter("Subscription");
		
		System.out.println("User: " + user + " TOKEN: " + token + "PDV: "+ pdv);
		System.out.println("Values " + PDV_Attribute_Name + " " + Project_ID + " " +PDV_Attribute_ID + " " +PDV_Attribute_Values + " " +Subscription);
		
		PDV_Attribute_Values = PDV_Attribute_Values.substring(0, PDV_Attribute_Values.length()-1) +","+pdv +")";
		
		System.out.println("PDV_Attribute_Values UPDATE :" + PDV_Attribute_Values);
		
		Properties prop = new Properties();
		  prop.load(getServletContext().getResourceAsStream("/WEB-INF/config.properties"));
		  System.out.println(prop.getProperty("url_DM"));
		  String url_DM = prop.getProperty("url_DM");
		  String protocol = prop.getProperty("protocol");
		  String port = prop.getProperty("port");
		
		  
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
		      
		      
				MediaType mediaType = MediaType.parse("application/json");
				
				String param ="";
				String request_start = "{\"User_Variables\": [";
				String request_middle ="{\"Attribute_Name\":\""+PDV_Attribute_Name+"\",\"Attribute_Values\": [\""+PDV_Attribute_Values+"\"]}";
				
				System.out.println(request_middle);
			 	String request_end = "],  \"Offers\": []}";
				
				String request_body = request_start+request_middle+request_end;
				
				System.out.println("Request : " + request_body);
				RequestBody body = RequestBody.create(mediaType, request_body);
				Request publish_form = new Request.Builder()
				  .url(protocol+"://"+url_DM+":"+port+"/collaborate/api/collaborate/v1/locals/"+Project_ID+"/projectforms/"+Subscription)
				  .put(body)
				  .addHeader("m_user_name", user)
				  .addHeader("m_tokenid", token)
				  .addHeader("authorization", "application/json")
				  .addHeader("api_auth_mode", "manager")
				  .addHeader("content-type", "application/json")
				  .addHeader("cache-control", "no-cache")
				  .addHeader("postman-token", "c4b9277d-4a72-a255-5121-cea02901ac7b")
				  .build();

				Response response_publish = client.newCall(publish_form).execute();   
				System.out.println("RESPONSE" + response_publish.body().toString());
		  } catch (GeneralSecurityException e) {
		  } 
	}
}