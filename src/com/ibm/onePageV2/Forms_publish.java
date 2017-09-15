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
 
public class Forms_publish extends HttpServlet { 
	 private static final long serialVersionUID = 1L;
     
	    public Forms_publish() {
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
		String id_Form = request.getParameter("id_form");
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
		String request_start = "{\"User_Variables\": [";
		String request_middle ="";
		String mailing_ID="";
		String magasin = "";
				while(list_parameters.hasMoreElements()){
					
					param = (String) list_parameters.nextElement();
					System.out.println("param Value " + param);
					if(((param.equals("values")))){
						String mid = request.getParameter(param);
						String[] list_param;
						list_param = mid.split("&");
						
						for(int i=0;i<list_param.length;i++)
						{
							String[] list_value = list_param[i].split("=");
							request_middle = request_middle + "{\"Attribute_Name\":\""+list_value[0]+"\",\"Attribute_Values\": [\""+list_value[1]+"\"]},";
						}
						
						
								
					}		
					
				}
		request_middle= request_middle.substring(0,request_middle.length()-1);
		System.out.println(request_middle);
	 	String request_end = "],  \"Offers\": []}";
		
		String request_body = request_start+request_middle+request_end;
		
		System.out.println("Request : " + request_body);
		RequestBody body = RequestBody.create(mediaType, request_body);
		Request publish_form = new Request.Builder()
		  .url(protocol+"://"+url_DM+":"+port+"/collaborate/api/collaborate/v1/locals/"+Campaign_ID+"/projectforms/"+id_Form)
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
		
		
		RequestBody body_status = RequestBody.create(mediaType, "{\"Project_Status\": \"START\"}");
		Request request_status = new Request.Builder()
		  .url(protocol+"://"+url_DM+":"+port+"/collaborate/api/collaborate/v1/locals/"+Campaign_ID+"/changestate")
		  .put(body_status)
		  .addHeader("m_user_name", user)
		  .addHeader("m_tokenid", token)
		  .addHeader("authorization", "application/json")
		  .addHeader("api_auth_mode", "manager")
		  .addHeader("content-type", "application/json")
		  .addHeader("cache-control", "no-cache")
		  .addHeader("postman-token", "698d50a4-d5bc-9337-a3b2-3d60d03d9ca3")
		  .build();

		Response response_status = client.newCall(request_status).execute();
		
		// Rest Call for Bluemix
		 // RestCall call2 = new RestCall();
		  //String retour2 = call.PostCall("http://"+url_DM+":7001/collaborate/api/collaborate/v1/locals/"+Campaign_ID+"/changestate","{\"Project_Status\": \"START\"}","application/json", user, token);

		//System.out.println("BODY Project " + retour2);
				
		//if(response_status.code()==204){
			System.out.println("Campagne RUNNING");
					
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
					if(((((JSONObject)jsonArray_Workflow.get(i)).get("Flowchart_Tab_Name")).toString().contains("Simu")))
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
		System.out.println("JOB " + "https://"+url_DM+":"+port+"/collaborate/api/collaborate/v1/locals/"+Campaign_ID+"/workflowrows/"+Row_ID+"/run/"+job);	
			
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
		System.out.println("Body Validation " + body);
		
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
		}catch (Exception e)
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
