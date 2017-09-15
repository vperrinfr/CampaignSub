package com.ibm.onePage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class ActionServlet
*/

public class IMC_preview extends HttpServlet {
 private static final long serialVersionUID = 1L;
    
    public IMC_preview() {
        // TODO Auto-generated constructor stub
    }

  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
  String mailingId=null;
  String username="";
  String pass ="";
  String magasin ="";
    
  mailingId = request.getParameter("mailing_ID");
  magasin = request.getParameter("magasin");
  
  if((mailingId=="")||(mailingId==null))
	{
	  mailingId="256932";
	}
  System.out.println("MailingId " + mailingId);
  Properties prop = new Properties();
  prop.load(getServletContext().getResourceAsStream("/WEB-INF/config.properties"));
  System.out.println(prop.getProperty("url_DM"));
  String url_DM = prop.getProperty("url_DM");
  String IMC_user = prop.getProperty("IMC_user");
  String IMC_password = prop.getProperty("IMC_password");
  String IMC_pod = prop.getProperty("IMC_pod");
  String sessionId="";
  String oauth="";
  String html_content=""; 
  //Login sequence
  String request1 = "<Envelope><Body><Login><USERNAME>"+IMC_user+"</USERNAME><PASSWORD>"+IMC_password+"</PASSWORD></Login></Body></Envelope>"; // put the actual XML here.
  System.out.println("RESQUET : " + request1);
  String sendEncoding = "utf-8";
  HttpURLConnection urlConn = null;
  OutputStream out = null;

    URL url = new URL("http://"+IMC_pod+"/XMLAPI");
    urlConn = (HttpURLConnection)url.openConnection();
    urlConn.setRequestMethod("POST");
    urlConn.setDoOutput(true);
    urlConn.setRequestProperty("Content-Type","text/xml;charset=" + sendEncoding);
    urlConn.connect(); 
    out = urlConn.getOutputStream();
    
    out.write(request1.getBytes(sendEncoding));
    out.flush();
    out.close();
  
    BufferedReader in = new BufferedReader(
	        new InputStreamReader(urlConn.getInputStream()));
	String inputLine;
	StringBuffer response_call = new StringBuffer();

	while ((inputLine = in.readLine()) != null) {
		response_call.append(inputLine);
	}
	in.close();

	//print result
	String to_parse = response_call.toString();
	System.out.println(to_parse);
	sessionId = to_parse.split("<SESSIONID>")[1].split("</SESSIONID>")[0];
	
	//Preview sequence
	String request2 = "<Envelope><Body><PreviewMailing><MailingId>"+mailingId+"</MailingId></PreviewMailing></Body></Envelope>"; // put the actual XML here.
	inputLine =""; 
	System.out.println(request2);
	to_parse="";
	String sessionStr = ";jsessionid=" +sessionId;
	
		url = new URL("http://api0.silverpop.com/XMLAPI" + sessionStr);
	    urlConn = (HttpURLConnection)url.openConnection();
	    urlConn.setRequestMethod("POST");
	    urlConn.setDoOutput(true);
	    urlConn.setRequestProperty("Content-Type","text/xml;charset=" + sendEncoding);
	    urlConn.connect();
	    out = urlConn.getOutputStream();
	    
	    out.write(request2.getBytes(sendEncoding));
	    out.flush();
	    out.close();
	  
	   in = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
		
		response_call = new StringBuffer();
  
		while ((inputLine = in.readLine()) != null) {
			response_call.append(inputLine);
		}
		in.close();

		//print result
		to_parse = response_call.toString();
		
		html_content = to_parse.split("<HTMLBody>")[1].split("]]>")[0];
		System.out.println("message : " + html_content);

		response.setContentType("text/plain");  
	  response.setCharacterEncoding("UTF-8"); 
	  response.getWriter().write(html_content); 
 }
  
 protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
  // TODO Auto-generated method stub
  
 }

}