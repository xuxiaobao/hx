package com.miaosu.util;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

public class SignatureUtils {

	private static final String NULL = "null";

	public static String getSignatureFromString(String baseString) throws IOException {

		// 使用MD5对待签名串求签
		byte[] bytes = null;
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			bytes = md5.digest(baseString.getBytes("UTF-8"));
		} catch (GeneralSecurityException ex) {
			throw new IOException(ex);
		}

		// 将MD5输出的二进制结果转换为小写的十六进制
		StringBuilder sign = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			String hex = Integer.toHexString(bytes[i] & 0xFF);
			if (hex.length() == 1) {
				sign.append("0");
			}
			sign.append(hex);
		}
		return sign.toString();
	}

	public static String getSignature(Map<String, String> params, String secret) throws IOException {
		// 先将参数以其参数名的字典序升序进行排序
		Map<String, String> sortedParams = new TreeMap<String, String>(params);
		Set<Entry<String, String>> entrys = sortedParams.entrySet();

		// 遍历排序后的字典，将所有参数按"key=value"格式拼接在一起
		StringBuilder basestring = new StringBuilder();
		for (Entry<String, String> param : entrys) {
			if (param.getValue() != null && (!param.getValue().trim().isEmpty())
					&& (!param.getValue().trim().equalsIgnoreCase(NULL))) {
				basestring.append(param.getKey().trim()).append("=").append(param.getValue().trim());
			}
		}
		basestring.append(secret);

		return getSignatureFromString(basestring.toString());

	}
	
	
	public static String getSignatureYuecheng(Map<String, String> params) throws IOException {
		// 先将参数以其参数名的字典序升序进行排序
		Map<String, String> sortedParams = new TreeMap<String, String>(params);
		Set<Entry<String, String>> entrys = sortedParams.entrySet();

		// 遍历排序后的字典，将所有参数按"key=value"格式拼接在一起
		StringBuilder basestring = new StringBuilder();
		for (Entry<String, String> param : entrys) {
			if (param.getValue() != null && (!param.getValue().trim().isEmpty())
					&& (!param.getValue().trim().equalsIgnoreCase(NULL))) {
				basestring.append(param.getKey().trim()).append("=").append(param.getValue().trim()).append("&");
			}
		}
//		basestring.deleteCharAt(basestring.length() - 1);
		basestring.append("key").append("=").append("0da7e2c41773078e0ee37ade52889885");
		return getSignatureFromString(basestring.toString());

	}
	
	
	public static String getSignatureLiudao(Map<String, String> params) throws IOException {
		// 先将参数以其参数名的字典序升序进行排序
//		Map<String, String> sortedParams = new TreeMap<String, String>(params);
//		Set<Entry<String, String>> entrys = sortedParams.entrySet();

		// 遍历排序后的字典，将所有参数按"key=value"格式拼接在一起
		StringBuilder basestring = new StringBuilder();
		for (String key : params.keySet()) {
			if (params.get(key) != null && (!params.get(key).trim().isEmpty())
					&& (!params.get(key).trim().equalsIgnoreCase(NULL))) {
				basestring.append(key.trim()).append("=").append(params.get(key).trim()).append("&");
			}
		}
//		basestring.deleteCharAt(basestring.length() - 1);
		String key = MD5Util.computeMD5("0e3eb26d15f942ae8840c577c27cee1f");
		basestring.append("key").append("=").append(key);
//		System.out.println("===basestring====="+basestring);
		return getSignatureFromString(basestring.toString());

	}
	/**
	 * 太永签名拼接及MD5加密
	 **/
	public static String getSignatureTaiyong(Map<String, String> params) throws IOException {
		// 先将参数以其参数名的字典序升序进行排序
		Map<String, String> sortedParams = new TreeMap<String, String>(params);
		Set<Entry<String, String>> entrys = sortedParams.entrySet();

		// 遍历排序后的字典，将所有参数按"key=value"格式拼接在一起
		StringBuilder basestring = new StringBuilder();
		for (Entry<String, String> param : entrys) {
			if (param.getValue() != null && (!param.getValue().trim().isEmpty())
					&& (!param.getValue().trim().equalsIgnoreCase(NULL))) {
				basestring.append(param.getKey().trim()).append("=").append(param.getValue().trim()).append("&");
			}
		}
		basestring.deleteCharAt(basestring.length() - 1);
		return getSignatureFromString(basestring.toString());

	}
	
	/**
	 *西安讯方洲签名 
	 **/
	public static String getSignatureXunfangzhou(String params) throws IOException {

		return getSignatureFromString(params);

	}
	
	/**
	 * 飞银（掌上流量）
	 **/
	public static String getSignatureFeiyin(Map<String, String> params) throws IOException {
		// 先将参数以其参数名的字典序升序进行排序
		Map<String, String> sortedParams = new TreeMap<String, String>(params);
		Set<Entry<String, String>> entrys = sortedParams.entrySet();

		// 遍历排序后的字典，将所有参数按"key=value"格式拼接在一起
		StringBuilder basestring = new StringBuilder();
		for (Entry<String, String> param : entrys) {
			if (param.getValue() != null && (!param.getValue().trim().isEmpty())
					&& (!param.getValue().trim().equalsIgnoreCase(NULL))) {
				basestring.append(param.getValue().trim());
			}
		}
		basestring.append("dK1c439KU274f9RK");
		return getSignatureFromString(basestring.toString());

	}
	

	public static String getSignature(String paramsStr, String secret) throws IOException {
		HashMap<String, String> result = new HashMap<String, String>();
		String[] pss = paramsStr.split("&");
		for (String ps : pss) {
			String[] tmp = ps.split("=");
			if (tmp.length > 1) {
				result.put(tmp[0].trim(), tmp[1].trim());
			} else {
				result.put(tmp[0].trim(), null);
			}
		}
		return getSignature(result, secret);
	}

	public static void main(String[] args) throws IOException {
		// String paramString =
		// "transactionId=44d8fd497b3f4bc18511296fd1c01670&corp_id=1001&plat_id=1001&act_id=1001&msisdn=13910217942&flow_value=100&serial_num=19001";
		// String secret = "test";

		String paramString = "act_id=1&cash_type=1111&channel_id=99991&channel_order_id=1111&msisdn=15952061111&product_code=100&req_type=1111&sub_channel_id=9527&trans_id=35706634a5f1dec44ee7ff1846846ba3";
		String secret = "*P*+nzss7l;rKXGT";
		if (args != null && args.length > 0) {
			paramString = args[0];
		}
		System.out.println(paramString);
		System.out.println(getSignature(paramString, secret));

//		HashMap<String, String> params = new HashMap<String, String>();
//		params.put("b", "b");
//		params.put("a", null);
//		getSignature(params, "test");
//		
//		String baseString = "104004400440042015111000001700200test";
//		String sign = getSignatureFromString(baseString);
//		System.out.println("getSignatureFromString=" + sign);
	}
}
