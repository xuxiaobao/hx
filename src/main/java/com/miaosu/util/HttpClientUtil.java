package com.miaosu.util;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.*;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.nio.charset.CodingErrorAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * create by xxb
 */
public class HttpClientUtil {
	private static final String encoding = "UTF-8";
	private final static int connectTimeout = 40000;

	private static PoolingHttpClientConnectionManager connManager = null;
	private static CloseableHttpClient httpclient = null;

	static {
		try {
			Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory> create().register("http",
					PlainConnectionSocketFactory.getSocketFactory()).register("https", SSLConnectionSocketFactory.getSocketFactory()).build();
			connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
			httpclient = HttpClients.custom().setConnectionManager(connManager).build();
			// Create socket configuration
			SocketConfig socketConfig = SocketConfig.custom().setTcpNoDelay(true).build();
			connManager.setDefaultSocketConfig(socketConfig);
			// Create message constraints
			MessageConstraints messageConstraints = MessageConstraints.custom().setMaxHeaderCount(200).setMaxLineLength(2000).build();
			// Create connection configuration
			ConnectionConfig connectionConfig = ConnectionConfig.custom().setMalformedInputAction(CodingErrorAction.IGNORE).setUnmappableInputAction(
					CodingErrorAction.IGNORE).setCharset(Consts.UTF_8).setMessageConstraints(messageConstraints).build();
			connManager.setDefaultConnectionConfig(connectionConfig);
			connManager.setMaxTotal(200);
			connManager.setDefaultMaxPerRoute(20);
		} catch (Exception e) {

		}
	}
	public static Map<String, String> doPost(String url_str, String param) {
		return doPost(url_str,param,"", "");
	}

	public static Map<String, String> doPost(String url_str, String param,String token, String sign) {
		Map<String, String> map = new HashMap<String, String>();
		HttpPost post = new HttpPost(url_str);
		try {
			post.setHeader("User-Agent", "agx.ims");
			post.setHeader("Content-Type", "application/xml");
			post.setHeader("4GGOGO-Auth-Token", token);
			post.setHeader("HTTP-X-4GGOGO-Signature", sign);
			RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(connectTimeout).setConnectTimeout(connectTimeout)
					.setConnectionRequestTimeout(connectTimeout).setExpectContinueEnabled(false).build();
			post.setConfig(requestConfig);
			post.setEntity(new StringEntity(param, encoding));
			CloseableHttpResponse response = httpclient.execute(post);
			map.put("status", String.valueOf(response.getStatusLine().getStatusCode()));
			System.out.println("post======="+response.getStatusLine().getStatusCode());
			try {
				if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode()) {
					HttpEntity entity = response.getEntity();
					try {
						if (entity != null) {
							map.put("response", EntityUtils.toString(entity, encoding));
							return map;
						}
					} finally {
						if (entity != null) {
							entity.getContent().close();
						}
					}
				}
			} finally {
				if (response != null) {
					response.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			post.releaseConnection();
		}
		return null;
	}
	public static Map<String, String> doGet(String url_str) {
		return doGet(url_str,"","");
	}
	public static Map<String, String> doGet(String url_str,String token, String sign) {
		Map<String, String> map = new HashMap<String, String>();
		HttpGet get = new HttpGet(url_str);
		try {
			get.setHeader("User-Agent", "agx.ims");
			get.setHeader("Content-Type", "application/xml");
			get.setHeader("4GGOGO-Auth-Token", token);
			get.setHeader("HTTP-X-4GGOGO-Signature", sign);
			RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(connectTimeout).setConnectTimeout(connectTimeout)
					.setConnectionRequestTimeout(connectTimeout).setExpectContinueEnabled(false).build();
			get.setConfig(requestConfig);
			CloseableHttpResponse response = httpclient.execute(get);
			map.put("status", String.valueOf(response.getStatusLine().getStatusCode()));
			System.out.println("get======="+response.getStatusLine().getStatusCode());
			try {
				HttpEntity entity = response.getEntity();
				try {
					if (entity != null) {
						map.put("response", EntityUtils.toString(entity, encoding));
						return map;
					}
				} finally {
					if (entity != null) {
						entity.getContent().close();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (response != null) {
					response.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			get.releaseConnection();
		}
		return null;
	}

	/**
	 * HTTPS请求，默认超时为5S
	 * 
	 * @param reqURL
	 * @param params
	 * @return
	 */
	public static String connectPostHttps(String reqURL, Map<String, String> params) {

		String responseContent = null;

		HttpPost httpPost = new HttpPost(reqURL);
		try {
			RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(connectTimeout).setConnectTimeout(connectTimeout)
					.setConnectionRequestTimeout(connectTimeout).build();

			List<NameValuePair> formParams = new ArrayList<NameValuePair>();
			httpPost.setEntity(new UrlEncodedFormEntity(formParams, Consts.UTF_8));
			httpPost.setConfig(requestConfig);
			// 绑定到请求 Entry
			for (Map.Entry<String, String> entry : params.entrySet()) {
				formParams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
			}
			CloseableHttpResponse response = httpclient.execute(httpPost);
			try {
				// 执行POST请求
				HttpEntity entity = response.getEntity(); // 获取响应实体
				try {
					if (null != entity) {
						responseContent = EntityUtils.toString(entity, Consts.UTF_8);
					}
				} finally {
					if (entity != null) {
						entity.getContent().close();
					}
				}
			} finally {
				if (response != null) {
					response.close();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			httpPost.releaseConnection();
		}
		return responseContent;

	}

}