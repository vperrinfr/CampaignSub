package com.ibm.onePageV2;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import javax.naming.Context;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import java.security.cert.X509Certificate;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
 
/** 
 * Servlet implementation class ActionServlet
*/ 

public class AuthServlet extends HttpServlet {
 private static final long serialVersionUID = 1L;
private static final long LOGIN_TIMEOUT_SEC = 0;
      
    public AuthServlet() {
        // TODO Auto-generated constructor stub
    }  

  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {


	  Properties prop = new Properties();
	  prop.load(getServletContext().getResourceAsStream("/WEB-INF/config.properties"));
	  System.out.println(prop.getProperty("url_DM"));
	  String url_DM = prop.getProperty("url_DM");
	  String protocol = prop.getProperty("protocol");
	  String port = prop.getProperty("port");
	  
	  final MediaType JSON  = MediaType.parse("application/json; charset=utf-8");
	  RequestBody body = RequestBody.create(null, new byte[0]);
	    
	  String user = request.getParameter("user");
	  String password = request.getParameter("password");
	  
	  
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
	      
		  Request request_auth = new Request.Builder()
		    .url(protocol+"://"+url_DM+":"+port+"/unica/api/manager/authentication/login")
		    .post(body)
		    .addHeader("m_user_name", user)
		    .addHeader("m_user_password", password)
		    .addHeader("cache-control", "no-cache")
		    .build();
	  
		  System.out.println("URL : "+ protocol+"://"+url_DM+":"+port+"/unica/api/manager/authentication/login");
		  
		  Response response_auth = client.newCall(request_auth).execute();
		  JSONObject jsonObj = (JSONObject) JSONValue.parse(response_auth.body().string());
		  System.out.println("jsonObj" + jsonObj);
	      String token = (String) jsonObj.get("m_tokenId");
	      response.setContentType("text/plain");
		  response.getWriter().write(token);
		  
	  } catch (GeneralSecurityException e) {
	  } 
	  
  }
  
 protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
  // TODO Auto-generated method stub
  
 }


}
