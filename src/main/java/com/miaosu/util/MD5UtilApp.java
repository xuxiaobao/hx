package com.miaosu.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5UtilApp {
  	static Logger log = LoggerFactory.getLogger(MD5UtilApp.class);
	public static String strToMd5(String str, String charSet) {
    String md5Str = null; 
    if (str != null && str.length() != 0)
    {
    try{
       MessageDigest md = MessageDigest.getInstance("MD5");
       md.update(str.getBytes(charSet));
       byte b[] = md.digest();
       int i;
       StringBuffer buf = new StringBuffer("");
       for (int offset = 0; offset < b.length; offset++)
       {
    	   i = b[offset];
    	   if (i < 0) 
    		   i+=256;
    	   if (i < 16) 
    		   buf.append("0");
    	   		buf.append(Integer.toHexString(i));
       }
       	md5Str = buf.toString();

		}
    	catch (NoSuchAlgorithmException e)
		{
    		log.error("MD5 加密发生异常。加密串："+ str);
		}
		catch (UnsupportedEncodingException e2)
		{
			log.error("MD5 加密发生异常。加密串："+str);
		}
    }
    return md5Str;
  }
		
 /**
 * 拼接参数
 *
 *@param params
 *@return
 */ 
 public static String getSign(String... params) 
		{
		StringBuilder sb = new StringBuilder();
		 for (String param : params) 
		 { 
		 sb.append(param); 
		 } 
		  return sb.toString(); 
		 } 
		 
 /**
 *MD5加密调用
 */ 
 public static String getSignAndMD5(String... params){ 
	 String sign = getSign(params); 
	 return strToMd5(sign, "utf-8"); 
 	}
}