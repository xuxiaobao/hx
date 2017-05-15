package com.miaosu.service.recharge.sup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.miaosu.base.ResultCode;
import com.miaosu.base.ServiceException;
import com.miaosu.model.Order;
import com.miaosu.model.enums.RechargeState;
import com.miaosu.service.orders.AbstractOrderService;
import com.miaosu.service.recharge.AbstractRecharge;
import com.miaosu.service.recharge.RechargeResult;
import com.miaosu.service.recharge.RechargeService;
import com.miaosu.util.SignatureUtils;


import java.text.SimpleDateFormat;
import com.miaosu.service.products.ProductService;
import com.miaosu.model.Product;
import com.miaosu.model.ProductDetail;

/**
 * 类的描述
 * 
 * @author wx
 * @Time 2016/2/18
 */
@Service
public class XunfangzhouRecharge extends AbstractRecharge
{

	private Logger logger = LoggerFactory.getLogger(getClass());

//	@Value("${zhuowang.channel}")
	private String channelId = "DSV00003";

//	@Value("${zhuowang.password}")
	private String password = "9eh1@1%wnv67$wlv";
	//测试平台
//	@Value("${zhuowang.server}")
//	private String SERVER_URL = "http://117.34.95.93:9999/foss/buy.html";
	//生产平台	
	private String SERVER_URL = "http://117.34.95.93:8080/foss/buy.html";
	private String SERVER_QUERY_URL = "http://117.34.95.93:8080/foss/query.html";
	@Autowired
	private AbstractOrderService abstractOrderService;
	
	@Autowired
	private RechargeService rechargeService;
	
	@Autowired
    private ProductService productService;

	@Override
	public void recharge(Order order)
	{
		logger.info("开始进行充值：{}", order.getId());
		long begin = System.currentTimeMillis();
		
//		channelId	必填，char(6)	分包平台分配给第三方的渠道编号
//		
//		timeStamp	必填，yyyyMMddHHmmss	时间戳，精确到秒
//				
//		productId	必填，char(6)	平台规定的产品编号，详见附录：产品编号对
//				照信息。
//				
//		mobile	必填	手机号码
//				
//		orderNo	必填，varchar(26)	第三方业务系统的唯一流水号，生成规则为 6
//				位 channelId+14 位时间戳+6 位序号，例如:
//				10000120150609125534000001
//				
//		sign	必填	接口调用认证签名；sign=MD5(channelId
//				+timeStamp +mobile +orderNo +Key)；md5采
//				用32位加密，全部小写。其中Key为平台提供给
//				第三方渠道的密钥；timeStamp与标准时间差不
//				能超过5分钟。

		Map<String, String> map = new HashMap<String, String>();
		
		channelId = "DSV00003";
		map.put("channelId", channelId);//		channelId	必填，char(6)	分包平台分配给第三方的渠道编号
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");//设置日期格式
		String timeStamp = df.format(new Date());// new Date()为获取当前系统时间
		map.put("timeStamp", timeStamp);//		timeStamp	必填，yyyyMMddHHmmss	时间戳，精确到秒
		map.put("productId", order.getProductId());//		productId	必填，char(6)	平台规定的产品编号，详见附录：产品编号对照信息。
		map.put("mobile", order.getPhone());//		mobile	必填	手机号码
		map.put("orderNo", order.getId());//		orderNo	必填，varchar(26)	第三方业务系统的唯一流水号，生成规则为 6位 channelId+14 位时间戳+6 位序号，例如:10000120150609125534000001	
//		map.put("key", "0da7e2c41773078e0ee37ade52889885");
		//测试平台KEY
//		String key = "53740ad4310b404f8acf5d78b2b0172e";
		//生产平台KEY
		String key = "f339ae3e02114743bda49813b4cc8793";
		String signature = "";
		try
		{
			signature = SignatureUtils.getSignatureXunfangzhou(channelId+timeStamp+order.getPhone()+order.getId()+key);
		}
		catch (IOException e)
		{
			logger.warn("签名异常{}", e);
			throw new ServiceException(ResultCode.FAILED);
		}
		map.put("sign", signature);//		sign	必填	接口调用认证签名；sign=MD5(channelId+timeStamp +mobile +orderNo +Key)；md5采用32位加密，全部小写。其中Key为平台提供给第三方渠道的密钥；timeStamp与标准时间差不能超过5分钟。
		JSONObject result = null;
		try
		{
			CloseableHttpClient httpClient = HttpClients.custom().build();
			HttpPost httpPost = new HttpPost(SERVER_URL);

			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			for (Entry<String, String> entry : map.entrySet())
			{
				nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
			}
			httpPost.setEntity(new UrlEncodedFormEntity(nvps));

			HttpResponse response = httpClient.execute(httpPost);
			HttpEntity entity = response.getEntity();
			if (entity != null)
			{
				String resultString = EntityUtils.toString(entity);
				logger.info("xunfangzhou 充值同步返回结果{}", resultString);
				result = JSON.parseObject(resultString);
			}
		}
		catch (Exception ex)
		{
			logger.warn("订购失败,exMsg:{}; costTime:{}", ex.getMessage(), (System.currentTimeMillis() - begin));
			throw new ServiceException(ResultCode.FAILED);
		}
		finally
		{
			logger.info("订购结束; result:{}; costTime:{}", result, (System.currentTimeMillis() - begin));
		}

		if (result.getString("resultCode") != null)
		{
			if ("0001".equals(result.getString("resultCode")))
			{
				logger.info("orderId:{},订购成功", order.getId());

				/*String rechargId = result.getJSONObject("content").getString("order_id");
				if (StringUtils.isEmpty(rechargId))
				{
					logger.warn("zhuowang充值返回rechargeId为空");
					throw new ServiceException(ResultCode.FAILED);
				}*/
				abstractOrderService.setRechargeId(order.getId(), "", "xunfangzhou");
			}
			else
			{
				String failedReason = result.getString("resultMsg");
				// 订购失败
				logger.info("{}充值失败，失败原因{}", order.getId(), failedReason);
//				rechargeService.rechargeSuccess(order.getId(), order.getUsername(), order.getNotifyUrl(), "N", failedReason, order.getExternalId());
				rechargeService.rechargeFailed(order.getId(), order.getUsername(), order.getNotifyUrl(),failedReason, order.getExternalId());
			}
		}
		else
		{
			logger.warn("订购返回结果为null");
			throw new ServiceException(ResultCode.FAILED);
		}
	}

	@Override
	public void queryResult(Order order)
	{
		JSONObject result = null;
		long begin = System.currentTimeMillis();

		HashMap<String, String> map = new HashMap<String, String>();
		channelId = "DSV00003";
		map.put("channelId", channelId);//		channelId	必填，char(6)	分包平台分配给第三方的渠道编号
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");//设置日期格式
		String timeStamp = df.format(new Date());// new Date()为获取当前系统时间
		map.put("timeStamp", timeStamp);//		timeStamp	必填，yyyyMMddHHmmss	时间戳，精确到秒
		map.put("orderNo", order.getId());//		orderNo	必填，varchar(26)	第三方业务系统的唯一流水号，生成规则为 6位 channelId+14 位时间戳+6 位序号，例如:10000120150609125534000001	
		map.put("mobile", order.getPhone());//		mobile	必填	手机号码
		String sign = "";
		//生产平台KEY
		String key = "f339ae3e02114743bda49813b4cc8793";
		try
		{
			sign = SignatureUtils.getSignatureXunfangzhou(channelId+timeStamp+order.getId()+key);
		}
		catch (IOException e)
		{
			logger.warn("签名异常{}", e);
			throw new ServiceException(ResultCode.FAILED);
		}
		map.put("sign", sign);

//		StringBuilder sb = new StringBuilder();
//		sb.append(SERVER_URL).append("rest/query?").append("channel_order_ids=").append(order.getId()).append("&channel_id=")
//				.append(channelId).append("&trans_id=").append(map.get("trans_id")).append("&sign=").append(map.get("sign"));
		try
		{
//			CloseableHttpClient httpClient = HttpClients.custom().build();
//			HttpGet httpGet = new HttpGet(sb.toString());
//			HttpResponse response = httpClient.execute(httpGet);
//			HttpEntity entity = response.getEntity();
//			if (entity != null)
//			{
//				String resutString = EntityUtils.toString(entity);
//				logger.info("zhuowang 充值查询返回结果{}", resutString);
//				result = JSON.parseObject(resutString);
//			}
			CloseableHttpClient httpClient = HttpClients.custom().build();
			HttpPost httpPost = new HttpPost(SERVER_QUERY_URL);

			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			for (Entry<String, String> entry : map.entrySet())
			{
				nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
			}
			httpPost.setEntity(new UrlEncodedFormEntity(nvps));

			HttpResponse response = httpClient.execute(httpPost);
			HttpEntity entity = response.getEntity();
			if (entity != null)
			{
				String resultString = EntityUtils.toString(entity);
				logger.info("xunfangzhou 充值查询返回结果{}", resultString);
				result = JSON.parseObject(resultString);
			}
		}
		catch (Exception ex)
		{
			logger.warn("查询订单失败, exMsg:{}; costTime:{}", ex.getMessage(), (System.currentTimeMillis() - begin));
			throw new ServiceException(ResultCode.FAILED);
		}
		finally
		{
			logger.info("查询订单结束; result:{}; costTime:{}", result, (System.currentTimeMillis() - begin));
		}

		if (result != null && "0000".equals(result.getString("resultCode")))
		{
			if ("0000".equals(result.getString("resultCode")))
			{
				logger.info("orderId：{}订购成功", order.getId());
				rechargeService.rechargeSuccess(order.getId(), order.getUsername(), order.getNotifyUrl(), "Y", "订购成功", order.getExternalId());
			}else if("0001".equals(result.getString("resultCode"))){
				//待充值，等着
			}
			else
			{
				logger.info("orderId：{}订购失败", order.getId());

				String failReson = "";
				try
				{
					failReson = result.getString("resultMsg");
				}
				catch (Exception e)
				{
					logger.warn("orderId：{}获取失败原因元素不存在", order.getId());
				}
//				rechargeService.rechargeSuccess(order.getId(), order.getUsername(), order.getNotifyUrl(), "N", failReson, order.getExternalId());
				rechargeService.rechargeFailed(order.getId(), order.getUsername(), order.getNotifyUrl(),failReson, order.getExternalId());
			}
		}
		else
		{
			logger.warn("订单查询返回结果为null");
			throw new ServiceException(ResultCode.FAILED);
		}
	}

	@Override
	public void callBack(RechargeResult rechargeResult)
	{
		try
		{
			// 根据充值单号查询三天内的订单信息
			Order order = abstractOrderService.findByCreateTimeAfterAndOrderId(
					new Date(System.currentTimeMillis() - 3 * 24 * 60 * 60 * 1000), rechargeResult.getOrderId());

			if (order == null)
			{
				logger.warn("未找到充值单号为{}的订单", rechargeResult.getRechargeId());
			}
			else
			{
				if (order.getRechargeState().getOper() == RechargeState.PROCESS.getOper())
				{
					rechargeService.rechargeSuccess(order.getId(), order.getUsername(), order.getNotifyUrl(), rechargeResult.getCode(),
							rechargeResult.getMsg(), order.getExternalId());
				}
			}
		}
		catch (Exception ex)
		{
			logger.warn("处理充值结果通知失败：{}， exMsg:{}", ex.getMessage());
		}
	}
	
	
/*	//与C#兼容的MD5加密算法
	  public static string GetMD5(String s)
	  {
	    MD5 md5 = new MD5CryptoServiceProvider();
	    byte[] t = md5.ComputeHash(Encoding.GetEncoding("utf-8").GetBytes(s));
	    StringBuilder sb = new StringBuilder(32);
	    for (int i = 0; i < t.Length; i++)
	    {
	      sb.Append(t[i].ToString("x").PadLeft(2, '0'));
	    }
	    return sb.ToString();
	  }*/
	
}
