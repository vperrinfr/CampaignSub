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
import java.util.Enumeration;
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
 
public class Forms_exec extends HttpServlet { 
	 private static final long serialVersionUID = 1L;
     
	    public Forms_exec() {
	        // TODO Auto-generated constructor stub
	    }  

	@SuppressWarnings("unchecked")
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		  Properties prop = new Properties();
		  prop.load(getServletContext().getResourceAsStream("/WEB-INF/config.properties"));
		  System.out.println(prop.getProperty("url_DM"));
		  String url_DM = prop.getProperty("url_DM");
		  String protocol = prop.getProperty("protocol");
		  String port = prop.getProperty("port");
		  String default_mailing = prop.getProperty("default_mailing");
		
		Enumeration<String> list_parameters= request.getParameterNames();
		String Attribute_ID ="";
		
		
		String user = request.getParameter("user");
		String token = request.getParameter("token");
		String Campaign_ID = request.getParameter("campaign_ID");
		String Row_ID="NO";
		String job = "";

		
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
		      
		MediaType mediaType = MediaType.parse("application/json");
		
		String param ="";
							
			Request request_workflow = new Request.Builder()
			  .url(protocol+"://"+url_DM+":"+port+"/collaborate/api/collaborate/v1/locals/"+Campaign_ID+"/workflowrows/")
			  .get()
			  .addHeader("m_user_name", user)
			  .addHeader("m_tokenid", token)
			  .addHeader("authorization", "application/json")
			  .addHeader("api_auth_mode", "manager")
			  .addHeader("content-type", "application/json")
			  .addHeader("cache-control", "no-cache")
			  .addHeader("postman-token", "1903bdc2-de54-d7d6-0688-d555c2b8fc0c")
			  .build();

			Response response_workflow = client.newCall(request_workflow).execute();
			String body_workflow = response_workflow.body().string();
			

			
			System.out.println("body_workflow :  " + body_workflow);
			JSONArray jsonArray_Workflow= (JSONArray) JSONValue.parse(body_workflow);
			
			for(int i=0;i<jsonArray_Workflow.size();i++)
			{
				if(((JSONObject)jsonArray_Workflow.get(i)).get("Flowchart_Tab_Name")!=null){
					if(((((JSONObject)jsonArray_Workflow.get(i)).get("Flowchart_Tab_Name")).equals("Exec")))
					{
						Row_ID = (((JSONObject)jsonArray_Workflow.get(i)).get("Row_ID")).toString();
						System.out.println("Row_ID" + Row_ID);
					}
				}
			}
			
			

			RequestBody body_run = RequestBody.create(mediaType, "{}");
			Request request_run = new Request.Builder()
			  .url(protocol+"://"+url_DM+":"+port+"/collaborate/api/collaborate/v1/locals/"+Campaign_ID+"/workflowrows/"+Row_ID+"/run")
			  .post(body_run)
			  .addHeader("m_user_name", user)
			  .addHeader("m_tokenid", token)
			  .addHeader("authorization", "application/json")
			  .addHeader("api_auth_mode", "manager")
			  .addHeader("content-type", "application/json")
			  .addHeader("cache-control", "no-cache")
			  .addHeader("postman-token", "717e6519-fecb-b817-0838-c8d82aaabf0d")
			  .build();

			Response response_run = client.newCall(request_run).execute();
			int status = response_run.code();
			if (status==201){
				String LOCATION = response_run.headers().get("Location");
				job = LOCATION.split("run/")[1];
				System.out.println("Job ID " + job);
			
			}else
			{
				System.out.println("Status Job" + status);
				String body_error = response_run.body().string();
				System.out.println("body_error :  " + body_error);
			}  
		  
			response_run.body().close();
		boolean workflow_OK=false;
		String body2 ="";
		int test=0;
		while(!workflow_OK)
		{	
		System.out.println("JOB " + "http://"+url_DM+":"+port+"/collaborate/api/collaborate/v1/locals/"+Campaign_ID+"/workflowrows/"+Row_ID+"/run/"+job);	
			
		Request request_call = new Request.Builder()
		  .url(protocol+"://"+url_DM+":"+port+"/collaborate/api/collaborate/v1/locals/"+Campaign_ID+"/workflowrows/"+Row_ID+"/run/"+job)
		  .get()
		  .addHeader("m_user_name", user)
		  .addHeader("m_tokenid", token)
		  .addHeader("authorization", "application/json")
		  .addHeader("api_auth_mode", "manager")
		  .addHeader("cache-control", "no-cache")
		  .build(); 

		Response response_call = client.newCall(request_call).execute();
		body2 = response_call.body().string();
		System.out.println("Body Validation " + body2);
		
		JSONObject jsonArray_Workflow2= (JSONObject) JSONValue.parse(body2);
		try{	
		if((jsonArray_Workflow2.get("Flowchart_Status").equals("QUEUED"))||(jsonArray_Workflow2.get("Flowchart_Status").equals("RUNNING")))
			{
				workflow_OK=false;	
			}
			else 
			{
				if(test==0)
				{
					workflow_OK=false;
				}
				else
				{
					workflow_OK=true;
				}
				test++;
			}
		response_call.body().close();
		}
		catch (Exception e)
			{
			body2 = "NotGood";
			System.out.println("Error " + e);
			workflow_OK=true;
			}
		}
			
		response.setContentType("text/plain");  
		response.setCharacterEncoding("UTF-8"); 
		response.getWriter().write(body2);	
			
		
		} catch (GeneralSecurityException e) {
	  } 
		
	}	
	 protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		  // TODO Auto-generated method stub
		 
		  
		 }
	
}
