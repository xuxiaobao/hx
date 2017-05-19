package com.miaosu.util;

import org.apache.log4j.Logger;

import javax.net.ssl.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * http请求实用类
 * @author hades
 *
 */
public final class HttpUtilApp {
	/** 连接超时时间 */
	private static int _connectTimeOut_ = 60 * 1000;
	/** 读取超时时间 */
	private static int _readTimeOut_ = 60 * 1000;
	/** 日志记录对象性*/
	private static Logger log = Logger.getLogger(HttpUtilApp.class);
	/** get请求 */
	public static String HTTP_GET = "GET";
	/** post请求 */
	public static String HTTP_POST = "POST";
	private static TrustManager myX509TrustManager = new X509TrustManager() { 
	    @Override 
	    public X509Certificate[] getAcceptedIssuers() { 
	        return null; 
	    } 

	    @Override 
	    public void checkServerTrusted(X509Certificate[] chain, String authType) 
	    throws CertificateException { 
	    } 

	    @Override 
	    public void checkClientTrusted(X509Certificate[] chain, String authType) 
	    throws CertificateException { 
	    } 
	};
	
	private static class TrustAnyHostnameVerifier implements HostnameVerifier {
		@Override
		public boolean verify(String hostname, SSLSession session) {
		    return true;
		}
	}
	/**
	 * 执行get请求
	 * @param requestUrl
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public static String doGet(String requestUrl,Map<String,String> params) throws Exception{
		return httpRequest(requestUrl,HTTP_GET,params);
	}
	/**
	 * 执行post请求
	 * @param requestUrl
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public static String doPost(String requestUrl,Map<String,String> params) throws Exception{
		return httpRequest(requestUrl,HTTP_POST,params);
	}
	/**
	 * 执行post请求
	 * @param requestUrl
	 * @param params
	 * @param connectTimeOut
	 * @param readTimeOut
	 * @return
	 * @throws Exception
	 */
	public static String doPost(String requestUrl,Map<String,String> params,int connectTimeOut,int readTimeOut) throws Exception{
		return httpRequest(requestUrl,HTTP_POST,params,connectTimeOut,readTimeOut);
	}
	
	/**
	 * 
	 * @param requestUrl
	 * @param params
	 * @param connectTimeOut
	 * @param readTimeOut
	 * @return
	 * @throws Exception
	 */
	public static String doGet(String requestUrl,Map<String,String> params,int connectTimeOut,int readTimeOut) throws Exception{
		return httpRequest(requestUrl,HTTP_GET,params,connectTimeOut,readTimeOut);
	}
	/**
	 * 执行post请求
	 * @param requestUrl
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static String doPost(String requestUrl,String data) throws Exception{
		return httpRequest(requestUrl,HTTP_POST,data);
	}
	/**
	 * 执行post请求
	 * @param requestUrl
	 * @param data
	 * @param connectTimeOut
	 * @param readTimeOut
	 * @return
	 * @throws Exception
	 */
	public static String doPost(String requestUrl,String data,int connectTimeOut,int readTimeOut) throws Exception{
		return httpRequest(requestUrl,HTTP_POST,data,connectTimeOut,readTimeOut);
	}
	/**
	 * 
	 * @param requestUrl
	 * @param data
	 * @param connectTimeOut
	 * @param readTimeOut
	 * @return
	 * @throws Exception
	 */
	public static String doGet(String requestUrl,String data,int connectTimeOut,int readTimeOut) throws Exception{
		return httpRequest(requestUrl,HTTP_GET,data,connectTimeOut,readTimeOut);
	}
	/**
	 * Https访问
	 * @param requestUrl
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static String doPostToHttps(String requestUrl,String data) throws Exception{
		return httpRequestToHttps(requestUrl,HTTP_POST,data);
	}
	/**
	 * https访问
	 * @param requestUrl
	 * @param requestMethod
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static String httpRequestToHttps(String requestUrl,String requestMethod,String data) throws Exception{
		return httpRequestToHttps(requestUrl,requestMethod,data,_connectTimeOut_,_readTimeOut_);
	}
	/**
	 * https访问
	 * @param requestUrl
	 * @param requestMethod
	 * @param data
	 * @param connectTimeOut
	 * @param readTimeOut
	 * @return
	 * @throws Exception
	 */
	public static String httpRequestToHttps(String requestUrl,String requestMethod,String data,int connectTimeOut,int readTimeOut) throws Exception{
		String ret = null;
		HttpsURLConnection httpUrlConn = null;
		InputStream is = null;
		BufferedReader bReader = null;
		InputStreamReader isr = null;
	    try {
	    	log.debug("请求开始，请求url:" + requestUrl);
	    	SSLContext sc = SSLContext.getInstance("SSL");
	    	sc.init(null, new TrustManager[] {myX509TrustManager},new java.security.SecureRandom());
	        URL url = new URL(requestUrl);
	        httpUrlConn = (HttpsURLConnection)url.openConnection();
	        httpUrlConn.setSSLSocketFactory(sc.getSocketFactory());
	        httpUrlConn.setRequestMethod("POST"); 
	        httpUrlConn.setHostnameVerifier(new TrustAnyHostnameVerifier());
	        
	        //设定连接超时时间
	        httpUrlConn.setConnectTimeout(connectTimeOut);
	        //设置读取超时时间
	        httpUrlConn.setReadTimeout(readTimeOut);
	        //设置是否向httpUrlConnection输出
	        httpUrlConn.setDoOutput(true);
	        //设置是否从httpUrlConnection读入，默认情况下是true;
	        httpUrlConn.setDoInput(true);
	        
	        httpUrlConn.setUseCaches(false);
	        //设置请求方式（GET/POST）
	        httpUrlConn.setRequestMethod(requestMethod);
  
	        if(data != null && data.length() > 0){
	        	httpUrlConn.getOutputStream().write(data.getBytes("utf-8"));
	        }
	        
	        // 将返回的输入流转换成字符串
	        is = httpUrlConn.getInputStream();
	        isr = new InputStreamReader(is, "utf-8");
	        bReader = new BufferedReader(isr);
	        String str = null;
	        StringBuffer buffer = new StringBuffer();
	        while ((str = bReader.readLine()) != null) {
	            buffer.append(str).append("\n");
	        }
	        ret = buffer.toString();
	        log.debug("请求完成");
	    }finally{
	    	if(bReader != null){
	    		try{
	    			bReader.close();
	    			bReader = null;
	    		}catch(Exception e){
	    			
	    		}
	    	}
	    	if(isr != null){
	    		try{
	    			isr.close();
	    			isr = null;
	    		}catch(Exception e){
	    			
	    		}
	    	}
	    	if(is != null){
	    		try{
	    			is.close();
	    		    is = null;
	    		}catch(Exception e){
	    			
	    		}
	    	}
	    	if(httpUrlConn != null){
	    		try{
	    			httpUrlConn.disconnect();
	    			httpUrlConn = null;
	    		}catch(Exception e){
	    			
	    		}
	    	}
	    }
	    return ret;
	}
	/**
	 * 执行http请求
	 * @param requestUrl
	 * @param requestMethod
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static String httpRequest(String requestUrl,String requestMethod,String data) throws Exception{
		return httpRequest(requestUrl,requestMethod,data,_connectTimeOut_,_readTimeOut_);
	}
	/**
	 * 执行http请求
	 * @param requestUrl
	 * @param requestMethod
	 * @param data
	 * @return
	 * 
	 */
	public static String httpRequest(String requestUrl,String requestMethod,String data,int connectTimeOut,int readTimeOut) throws Exception{
		String ret = null;
		HttpURLConnection httpUrlConn = null;
		InputStream is = null;
		BufferedReader bReader = null;
		InputStreamReader isr = null;
	    try {
	    	log.debug("请求开始，请求url:" + requestUrl);
	        URL url = new URL(requestUrl);
	        if(requestUrl.startsWith("https://")){
	        	SSLContext sslcontext = SSLContext.getInstance("TLS"); 
	            sslcontext.init(null, new TrustManager[]{myX509TrustManager}, null);
	        	httpUrlConn = (HttpsURLConnection)url.openConnection();
	        	((HttpsURLConnection)httpUrlConn).setSSLSocketFactory(sslcontext.getSocketFactory());
	        	
	        }else{
	        	httpUrlConn=(HttpURLConnection)url.openConnection();
	        }
	        //设定连接超时时间
	        httpUrlConn.setConnectTimeout(connectTimeOut);
	        //设置读取超时时间
	        httpUrlConn.setReadTimeout(readTimeOut);
	        //设置是否向httpUrlConnection输出
	        httpUrlConn.setDoOutput(true);
	        //设置是否从httpUrlConnection读入，默认情况下是true;
	        httpUrlConn.setDoInput(true);
	        
	        httpUrlConn.setUseCaches(false);
	        //设置请求方式（GET/POST）
	        httpUrlConn.setRequestMethod(requestMethod);
  
	        if(data != null && data.length() > 0){
	        	httpUrlConn.getOutputStream().write(data.getBytes("utf-8"));
	        }
	        
	        // 将返回的输入流转换成字符串
	        is = httpUrlConn.getInputStream();
	        isr = new InputStreamReader(is, "utf-8");
	        bReader = new BufferedReader(isr);
	        String str = null;
	        StringBuffer buffer = new StringBuffer();
	        while ((str = bReader.readLine()) != null) {
	            buffer.append(str).append("\n");
	        }
	        ret = buffer.toString();
	        log.debug("请求完成");
	    }finally{
	    	if(bReader != null){
	    		try{
	    			bReader.close();
	    			bReader = null;
	    		}catch(Exception e){
	    			
	    		}
	    	}
	    	if(isr != null){
	    		try{
	    			isr.close();
	    			isr = null;
	    		}catch(Exception e){
	    			
	    		}
	    	}
	    	if(is != null){
	    		try{
	    			is.close();
	    		    is = null;
	    		}catch(Exception e){
	    			
	    		}
	    	}
	    	if(httpUrlConn != null){
	    		try{
	    			httpUrlConn.disconnect();
	    			httpUrlConn = null;
	    		}catch(Exception e){
	    			
	    		}
	    	}
	    }
	    return ret;
	}
	/**
	 * 执行http请求
	 * @param requestUrl 请求地址
	 * @param requestMethod 请求方法
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public static String httpRequest(String requestUrl, String requestMethod,Map<String,String> params) throws Exception{
	   return httpRequest(requestUrl,requestMethod,params,_connectTimeOut_,_readTimeOut_);
	}
	/**
	 * 
	 * @param requestUrl
	 * @param requestMethod
	 * @param params
	 * @param connectTimeOut
	 * @param readTimeOut
	 * @return
	 * @throws Exception
	 */
	public static String httpRequest(String requestUrl, String requestMethod,Map<String,String> params,int connectTimeOut,int readTimeOut) throws Exception{
	    String outStr = getParamsStr(params);
    	if(requestMethod.equalsIgnoreCase(HTTP_GET)){
    		requestUrl = buildReqUrl(requestUrl,outStr);
    		outStr = "";
    	}
	    return httpRequest(requestUrl,requestMethod,outStr,connectTimeOut,readTimeOut);
	}
	/**
	 * 根据map获取请求参数串
	 * @param params
	 * @return
	 */
	private static String getParamsStr(Map<String,String> params){
		StringBuilder sb = new StringBuilder("");
		if(params != null){
			Iterator<Map.Entry<String, String>> ite = params.entrySet().iterator();
			while(ite.hasNext()){
				Map.Entry<String, String> entry = ite.next();
				if(entry.getValue() != null){
					sb.append("&").append(entry.getKey()).append("=").append(entry.getValue());
				}
			} 
		}
		if(sb.length() > 1){
			return sb.toString().substring(1);
		}else{
			return "";
		}
	}
	/**
	 * 合成请求url
	 * @param requestUri
	 * @param params
	 * @return
	 */
	private static String buildReqUrl(String requestUri,String params){
		String retuUrl = requestUri;
		if(params.length() > 0){
			if(requestUri.indexOf("?") > 0){
				retuUrl += params.toString();
			}else{
				retuUrl += "?" + params;
			}
		}
		return retuUrl;
	}
	
//	/**
//	 * 接受HTTP的Multipart格式请求
//	 * @param url
//	 * @param entityMap
//	 * @return
//	 * @throws Exception
//	 */
//	public static String httpRequestMultipart(String url,Map<String,String> entityMap,Map<String,String> headerMap) throws Exception {
//		HttpResponse response = null;
//		try{
//			HttpClient httpclient = new DefaultHttpClient(); 
//			httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 150 * 1000); 
//			httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 150 * 1000); 
//			HttpPost httpPost = new HttpPost(url);
//			MultipartEntity entity = new MultipartEntity();
//			httpPost.setEntity(entity);
//			
//			//设置请求的头部
//			if (headerMap != null && !headerMap.isEmpty()) {
//				Iterator<Map.Entry<String,String>> itrHead = headerMap.entrySet().iterator();
//				while(itrHead.hasNext()) {
//					Map.Entry<String,String> entry = itrHead.next();
//					httpPost.addHeader(entry.getKey(), entry.getValue());
//				}
//			}
//			
//			//设置请求体
//			if (entityMap != null && !entityMap.isEmpty()) {
//				Iterator<Map.Entry<String,String>> itr = entityMap.entrySet().iterator();
//				
//				while(itr.hasNext()) {
//					Map.Entry<String,String> entry = itr.next();
//					if (entry.getValue() != null) {
//						entity.addPart(entry.getKey(), new StringBody(entry.getValue(), Charset.forName("UTF-8")));
//					}
//				}
//			}
//	        response = httpclient.execute(httpPost);
//		}catch(Exception e){
//			log.error("请求出错", e);
//			throw e;
//		}
//		return processMultipartResponse(response);
//	}
	
	/**
	 * 解析HTTP的Multipart格式的响应
	 * @param response
	 * @return
	 * @throws Exception
	 */
//	public static String processMultipartResponse(HttpResponse response) throws Exception {  
//		StringBuffer buffer = new StringBuffer();
//		
//        HttpEntity entity = response.getEntity();  
//        BufferedReader is = new BufferedReader(new InputStreamReader(entity.getContent(),"UTF-8"));  
//        String str = null;  
//        while((str = is.readLine()) != null){  
//        	buffer.append(str);  
//        }
//        return buffer.toString();
//    }
//	
	/**
	 * 解析类型是Multipart请求数据
	 * @param request
	 * @return
	 */
//	public static Map<String,String> analyzeRequestMultipart(HttpServletRequest request) {
//		Map<String,String> params = null;
//		try{
//			DiskFileItemFactory factory = new DiskFileItemFactory(); 
//			ServletFileUpload upload = new ServletFileUpload(factory); 
//			List items = upload.parseRequest(request); 
//			params = new HashMap<String,String>(); 
//			for(Object object:items){ 
//			    FileItem fileItem = (FileItem) object; 
//			    if (fileItem.isFormField()) { 
//			    	params.put(fileItem.getFieldName(), fileItem.getString("utf-8"));//如果你页面编码是utf-8的 
//			    } 
//			} 
//		}catch(Exception e){
//			log.error("解析Request出错", e);
//		}
//		return params;
//	}
	
	
	/**
	 * 读取请求数据
	 * @param request
	 * @return
	 * @throws IOException
	 */
	public static String readRequest(HttpServletRequest request) throws IOException{
		InputStream is = null;
		BufferedReader bReader = null;
		InputStreamReader isr = null;
		try{
			is = request.getInputStream();
			isr = new InputStreamReader(is, "utf-8");
	        bReader = new BufferedReader(isr);
	        String str = null;
	        StringBuffer buffer = new StringBuffer();
	        while ((str = bReader.readLine()) != null) {
	            buffer.append(str);
	        }
	        return buffer.toString();
		}finally{
			if(bReader != null){
				try{
					bReader.close();
				}catch(Exception e){
					
				}
			}
			if(isr != null){
				try{
					isr.close();
				}catch(Exception e){
					
				}
			}
			if(is != null){
				try{
					is.close();
				}catch(Exception e){
					
				}
			}
		}
	}
	/** 输出编码 */
	public final static String contentType = "application/json; charset=utf-8";
	public static void doResponse(HttpServletResponse response, String str){
		log.info("ResponseData:"+str);
		PrintWriter out = null;
		try{
			//屏蔽此代码，如果不屏蔽则无法保存session会话
//			response.reset();
			response.setContentType(contentType);
			out = response.getWriter();
			out.write(str);
			out.flush();
		}catch(Exception e){
			log.error("输出出错，输出字符串:" + str,e);
		} finally{
			try{
				if(out != null){
					out.close();
				}
			}catch(Exception e){
				
			}
		}
	}
	
	/**
	 * 
	 * @param request
	 * @return
	 */
	public static String getIpAddr(HttpServletRequest request) {
	     String ipAddress = null;   
	     //ipAddress = this.getRequest().getRemoteAddr();   
	     ipAddress = request.getHeader("x-forwarded-for");   
	     if(ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {   
	      ipAddress = request.getHeader("Proxy-Client-IP");   
	     }   
	     if(ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {   
	         ipAddress = request.getHeader("WL-Proxy-Client-IP");   
	     }   
	     if(ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {   
	    	 ipAddress = request.getRemoteAddr();   
	    	 if(ipAddress.equals("127.0.0.1")){   
	    		 //根据网卡取本机配置的IP   
	    		 InetAddress inet=null;   
	    		 try {   
		    	   inet = InetAddress.getLocalHost();   
	    		 } catch (UnknownHostException e) {   
	    			 
	    		 }   
	    		 ipAddress= inet.getHostAddress();   
		     }
		 }   
	  
	     //对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割   
	     if(ipAddress!=null && ipAddress.length()>15){ //"***.***.***.***".length() = 15   
	         if(ipAddress.indexOf(",")>0){   
	             ipAddress = ipAddress.substring(0,ipAddress.indexOf(","));   
	         }   
	     }   
	     return ipAddress;    
	  }
	 
	/**
	 * 常用浏览器信息
	 */
    private final static String IE9="msie 9.0";  
    private final static String IE8="msie 8.0";  
    private final static String IE7="msie 7.0";  
    private final static String IE6="msie 6.0";  
    private final static String MAXTHON="maxthon";  
    private final static String QQ="qqbrowser";  
    private final static String GREEN="greenbrowser";  
    private final static String SE360="360se";  
    private final static String FIREFOX="firefox";  
    private final static String OPERA="opera";  
    private final static String CHROME="chrome";  
    private final static String SAFARI="safari";  
    private final static String OTHER="其它"; 
    private final static String WECHAT="micromessenger";  
    
      
      
    public static String getDeviceName(HttpServletRequest request){
    	StringBuffer retSb = new StringBuffer();
    	String userAgent=request.getHeader("User-Agent").toLowerCase();
    	retSb.append("( ");
        if(regex(OPERA, userAgent)) 
        	retSb.append(OPERA);  
        else if(regex(CHROME, userAgent))
        	retSb.append(CHROME);   
        else if(regex(FIREFOX, userAgent))
        	retSb.append(FIREFOX);   
        else if(regex(SAFARI, userAgent))
        	retSb.append(SAFARI);   
        else if(regex(SE360, userAgent))
        	retSb.append(SE360);  
        else if(regex(GREEN,userAgent))
        	retSb.append(GREEN);  
        else if(regex(QQ,userAgent))
        	retSb.append(QQ); 
        else if(regex(WECHAT,userAgent))
        	retSb.append(WECHAT); 
        else if(regex(MAXTHON, userAgent))
        	retSb.append(MAXTHON);   
        else if(regex(IE9,userAgent))
        	retSb.append(IE9); 
        else if(regex(IE8,userAgent))
        	retSb.append(IE8); 
        else if(regex(IE7,userAgent))
        	retSb.append(IE7); 
        else if(regex(IE6,userAgent))
        	retSb.append(IE6); 
        
        return retSb.append(")").append(userAgent).toString();  
    }  
    
    private static boolean regex(String regex,String str){  
        Pattern p =Pattern.compile(regex,Pattern.MULTILINE);  
        Matcher m=p.matcher(str);  
        return m.find();  
    } 
	
}
