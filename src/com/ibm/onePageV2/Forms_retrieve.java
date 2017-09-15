package com.ibm.onePageV2;

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

public class Forms_retrieve extends HttpServlet { 
	 private static final long serialVersionUID = 1L;
     
	    public Forms_retrieve() {
	        // TODO Auto-generated constructor stub
	    }  

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String user = request.getParameter("user");
		String token = request.getParameter("token");
		String Campaign_ID = request.getParameter("campaign_ID");
		String id_Form= "";
		String id_Name= "";
		System.out.println("User: " + user + " TOKEN: " + token + "Campaign_ID: "+ Campaign_ID);
		
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

		Request request_call = new Request.Builder()
		  .url(protocol+"://"+url_DM+":"+port+"/collaborate/api/collaborate/v1/locals/"+Campaign_ID+"/projectforms")
		  .get()
		  .addHeader("m_user_name", user)
		  .addHeader("m_tokenid", token)
		  .addHeader("authorization", "application/json")
		  .addHeader("api_auth_mode", "manager")
		  .addHeader("cache-control", "no-cache")
		  .build(); 

		Response response_call = client.newCall(request_call).execute();
		String retour = response_call.body().string();
		
		  // Rest Call for Bluemix
		 // RestCall call = new RestCall();
		 // String retour = call.GETCall("http://"+url_DM+":7001/collaborate/api/collaborate/v1/locals/"+Campaign_ID+"/projectforms", "","application/json", user, token);
		  // End of Rest Call
		  
		JSONArray jsonObj = (JSONArray) JSONValue.parse(retour);
		System.out.println("body" + retour + "taille " + jsonObj.size());
		
		for(int i=0;i<jsonObj.size();i++)
		{			
			if (((JSONObject)jsonObj.get(i)).get("Tab_Name").toString().contains("Simu")) //HARD CODED
			{
				id_Form = ((JSONObject)jsonObj.get(i)).get("Form_ID").toString();
				id_Name = ((JSONObject)jsonObj.get(i)).get("Form_Name").toString();
			}
		} 
		
		Request request_form = new Request.Builder()
		  .url(protocol+"://"+url_DM+":"+port+"/collaborate/api/collaborate/v1/forms/"+id_Form+"/attributes")
		  .get()
		  .addHeader("m_user_name", user)
		  .addHeader("m_tokenid", token)
		  .addHeader("authorization", "application/json")
		  .addHeader("api_auth_mode", "manager")
		  .addHeader("cache-control", "no-cache")
		  .build(); 
		Response response_form = client.newCall(request_form).execute();
		String body_form = response_form.body().string();
		System.out.println("VALEUR Attributes "+body_form);
		
		
		// Rest Call for Bluemix
		 // RestCall call_2 = new RestCall();
		 // String body_form = call_2.GETCall("http://"+url_DM+":7001/collaborate/api/collaborate/v1/forms/"+id_Form+"/attributes", "","application/json", user, token);
		  // End of Rest Call
		
		response.setContentType("text/plain");  
		response.setCharacterEncoding("UTF-8"); 
		response.getWriter().write(id_Form +"/"+body_form);
		  } catch (GeneralSecurityException e) {
		  } 
	}
	
	 protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		  // TODO Auto-generated method stub
		  
		 }
	
}
