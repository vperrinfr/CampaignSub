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

public class Retrieve extends HttpServlet { 
	 private static final long serialVersionUID = 1L;
     
	    public Retrieve() {
	        // TODO Auto-generated constructor stub
	    }  

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String user = request.getParameter("user");
		String token = request.getParameter("token");
		String pdv = request.getParameter("pdv");
		String id_Form= "";
		String id_Name= "";
		System.out.println("User: " + user + " TOKEN: " + token + "PDV: "+ pdv);
		
		String form_id_description="";
		String form_id_subscription="";
		
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

		JSONArray obj_return = new JSONArray();  
		Request request_call = new Request.Builder()
		  .url(protocol+"://"+url_DM+":"+port+"/collaborate/api/collaborate/v1/locals/")
		  .get()
		  .addHeader("m_user_name", user)
		  .addHeader("m_tokenid", token)
		  .addHeader("authorization", "application/json")
		  .addHeader("api_auth_mode", "manager")
		  .addHeader("cache-control", "no-cache")
		  .build(); 

		Response response_call = client.newCall(request_call).execute();
		String retour = response_call.body().string();
		
		JSONArray jsonObj = (JSONArray) JSONValue.parse(retour);
		System.out.println("List" + retour + "taille " + jsonObj.size());
		
		for(int i=0;i<jsonObj.size();i++)
		{
			System.out.println(((JSONObject)jsonObj.get(i)).get("Description").toString());
			
			if (((JSONObject)jsonObj.get(i)).get("Description").toString().contains("abonnement")) 
			{
				JSONObject info_campaign = new JSONObject();
				System.out.println("Campaign Details "+ ((JSONObject)jsonObj.get(i)).toString());
				
				String project_id = ((JSONObject)jsonObj.get(i)).get("Project_ID").toString();
				info_campaign.put("Project_ID", project_id);
				
				String date_end = ((JSONObject)jsonObj.get(i)).get("End_Date").toString();
				info_campaign.put("End_Date", date_end);
				
				String date_start = ((JSONObject)jsonObj.get(i)).get("Start_Date").toString();
				info_campaign.put("Start_Date", date_start);
				
				String project_name = ((JSONObject)jsonObj.get(i)).get("Project_Name").toString();
				info_campaign.put("Project_Name", project_name);
				
				
				
				
				Request request_form = new Request.Builder()
				  .url(protocol+"://"+url_DM+":"+port+"/collaborate/api/collaborate/v1/locals/"+ project_id +"/projectforms")
				  .get()
				  .addHeader("m_user_name", user)
				  .addHeader("m_tokenid", token)
				  .addHeader("authorization", "application/json")
				  .addHeader("api_auth_mode", "manager")
				  .addHeader("cache-control", "no-cache")
				  .build(); 
				Response response_form = client.newCall(request_form).execute();
				String body_form = response_form.body().string();
				//System.out.println("VALEUR Project Forms "+body_form);
				
				JSONArray jsonObj2 = (JSONArray) JSONValue.parse(body_form);
				
				for(int y=0;y<jsonObj2.size();y++)
				{		
					//System.out.println("toto " + y);
					if (((JSONObject)jsonObj2.get(y)).get("Tab_Name").toString().contains("Description")) 
					{
						form_id_description = ((JSONObject)jsonObj2.get(y)).get("Form_ID").toString();
						info_campaign.put("Description", form_id_description);
						
						Request request_call_description = new Request.Builder()
						  .url(protocol+"://"+url_DM+":"+port+"/collaborate/api/collaborate/v1/locals/"+project_id+"/projectforms/" + form_id_description)
						  .get()
						  .addHeader("m_user_name", user)
						  .addHeader("m_tokenid", token)
						  .addHeader("authorization", "application/json")
						  .addHeader("api_auth_mode", "manager")
						  .addHeader("cache-control", "no-cache")
						  .build(); 

						Response response_call_description = client.newCall(request_call_description).execute();
						String retour_description = response_call_description.body().string();
						System.out.println("retour_description " + retour_description);
						
						JSONObject obj1 = (JSONObject) JSONValue.parse(retour_description);
						JSONArray obj2 = (JSONArray) obj1.get("User_Variables");
						

						for(int i2=0;i2<obj2.size();i2++)
						{
							if (((JSONObject)obj2.get(i2)).get("Attribute_Name").toString().contains("texte"))
							{
								String obj3 = ((JSONObject)(obj2.get(i2))).get("Attribute_Values").toString();
								obj3 = obj3.substring(2, obj3.length()-2);
								//System.out.println("OBJ " + obj3);
								info_campaign.put("Campaign_Description", obj3);
							}
							if (((JSONObject)obj2.get(i2)).get("Attribute_Name").toString().contains("url"))
							{
								String obj3 = ((JSONObject)(obj2.get(i2))).get("Attribute_Values").toString();
								obj3 = obj3.substring(2, obj3.length()-2);
								//System.out.println("OBJ " + obj3);
								info_campaign.put("Campaign_URL", obj3);
							}
						}
					}
					
					if (((JSONObject)jsonObj2.get(y)).get("Tab_Name").toString().contains("Subscription")) 
					{
						form_id_subscription = ((JSONObject)jsonObj2.get(y)).get("Form_ID").toString();
						info_campaign.put("Subscription", form_id_subscription);
						
						Request request_call_Subscription = new Request.Builder()
						  .url(protocol+"://"+url_DM+":"+port+"/collaborate/api/collaborate/v1/locals/"+project_id+"/projectforms/" + form_id_subscription)
						  .get()
						  .addHeader("m_user_name", user)
						  .addHeader("m_tokenid", token)
						  .addHeader("authorization", "application/json")
						  .addHeader("api_auth_mode", "manager")
						  .addHeader("cache-control", "no-cache")
						  .build(); 

						Response response_call_Subscription = client.newCall(request_call_Subscription).execute();
						String retour_Subscription = response_call_Subscription.body().string();
						//System.out.println("retour_Subscription " + retour_Subscription);
						
						JSONObject obj1 = (JSONObject) JSONValue.parse(retour_Subscription);
						JSONArray obj2 = (JSONArray) obj1.get("User_Variables");
						String obj3 = ((JSONObject)(obj2.get(0))).get("Attribute_Values").toString();
						String Attribute_ID = ((JSONObject)(obj2.get(0))).get("Attribute_ID").toString();
						String Attribute_Name = ((JSONObject)(obj2.get(0))).get("Attribute_Name").toString();
						String Attribute_Values = ((JSONObject)(obj2.get(0))).get("Attribute_Values").toString();
						Attribute_Values = Attribute_Values.substring(2, obj3.length()-2);
						//System.out.println("Attribute_Values " + Attribute_Values);
						info_campaign.put("PDV_Attribute_ID", Attribute_ID);
						info_campaign.put("PDV_Attribute_Name", Attribute_Name);
						info_campaign.put("PDV_Attribute_Values", Attribute_Values);
					}
				} 
				
				obj_return.add(info_campaign);
			}			
		}
			
		response.setContentType("text/plain");  
		response.setCharacterEncoding("UTF-8"); 
		response.getWriter().write(obj_return.toString());
		  } catch (GeneralSecurityException e) {
		  } 
	}
	
	 protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		  // TODO Auto-generated method stub
		  
		 }
	
}
