package com.miaosu.service.recharge.sup;

import java.io.IOException;
import java.nio.charset.Charset;
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
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
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
import com.alibaba.fastjson.JSONArray;

/**
 * 类的描述
 * 
 * @author wx
 * @Time 2016/2/18
 */
@Service
public class FeiyinRecharge extends AbstractRecharge
{

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Value("${zhuowang.channel}")
	private String channelId = "1000264";

	@Value("${zhuowang.password}")
	private String password = "9eh1@1%wnv67$wlv";

//	@Value("${zhuowang.server}")
	private String SERVER_URL = "http://l.palmflow.com:8081/flowfc/service/recharge";
	private String QUERY_SERVER_URL = "http://l.palmflow.com:8081/flowfc/service/get/status";

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
		
		

		Map<String, String> map = new HashMap<String, String>();
		
//		Product product = productService.get(order.getProductId());
//		List<ProductDetail> detailList = product.getProductDetailList();
//		String rangeStr = "";
//		String range = "0";
//		String flowValue = "";
//        for(ProductDetail detail : detailList)
//        {
//        	//流量类型
//        	if("2".equals(detail.getProId()))
//        	{
//        		rangeStr = detail.getProValue();
//        		if(rangeStr.equals("省内")){
//        			range = "1";
//        		}else{
//        			range = "0";
//        		}
//        	}
//        	//流量值
//        	if("5".equals(detail.getProId()))
//        	{
//        		flowValue =  detail.getProValue();
//        	}
//        }
//        logger.info("yuecheng 充值的流量{}", flowValue);
		
		
		
		
		
		
		
		map.put("transId", order.getId());//transId	每次交互的唯一标示，不能重复	String	50	必填	交易流水号，用于确保唯一性，生成方式可参见附录。
		map.put("userCode", "jiexint");//userCode	用户代码	String	20	必填	由流量平台分配
		map.put("channelOrderId", order.getId());//channelOrderId	合作方平台的订单ID，由合作方确保在自己的系统内部保持唯一	String	30	必填	用来判断是否是重复提交的订单
		map.put("phoneNum",order.getPhone());//phoneNum	充值手机号码	String	11	必填	
		
//		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");//设置日期格式
//		String timeStamp = df.format(new Date());// new Date()为获取当前系统时间
		map.put("cardCode", order.getProductId());//cardCode	充值卡品代码，该代码必须与掌上流量平台的代码一致	String	20	必填	
//		map.put("key", "0da7e2c41773078e0ee37ade52889885");
		String signature = "";
		try
		{
			signature = SignatureUtils.getSignatureFeiyin(map);
		}
		catch (IOException e)
		{
			logger.warn("签名异常{}", e);
			throw new ServiceException(ResultCode.FAILED);
		}
		map.put("sign", signature);//sign	数字签名	String		必填	数字签名规则1.3数据安全
		
		HttpClient httpClient = new DefaultHttpClient();  
        HttpPost method = new HttpPost(SERVER_URL);
		
		JSONObject result = null;
		try
		{
//			CloseableHttpClient httpClient = HttpClients.custom().build();
//			HttpPost httpPost = new HttpPost(SERVER_URL);
//
//			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
//			for (Entry<String, String> entry : map.entrySet())
//			{
//				nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
//			}
//			httpPost.setEntity(new UrlEncodedFormEntity(nvps));
//
//			HttpResponse response = httpClient.execute(httpPost);
//			HttpEntity entity = response.getEntity();
//			if (entity != null)
//			{
//				String resultString = EntityUtils.toString(entity);
//				logger.info("yuecheng 充值同步返回结果{}", resultString);
//				result = JSON.parseObject(resultString);
//			}
			
			JSONArray arry = new JSONArray();  
			JSONObject j = new JSONObject();  
//			j.put("orderId", "中文");  
//			j.put("createTimeOrder", "2015-08-11");
			j.put("transId", order.getId());//transId	每次交互的唯一标示，不能重复	String	50	必填	交易流水号，用于确保唯一性，生成方式可参见附录。
			j.put("userCode", "jiexint");//userCode	用户代码	String	20	必填	由流量平台分配
			j.put("channelOrderId", order.getId());//channelOrderId	合作方平台的订单ID，由合作方确保在自己的系统内部保持唯一	String	30	必填	用来判断是否是重复提交的订单
			j.put("phoneNum",order.getPhone());//phoneNum	充值手机号码	String	11	必填	
			j.put("cardCode", order.getProductId());
			j.put("sign", signature);//sign	数字签名	String		必填	数字签名规则1.3数据安全
			arry.add(j);
			String parameters = j.toJSONString();
            // 建立一个NameValuePair数组，用于存储欲传送的参数  
            method.addHeader("Content-type","application/json; charset=utf-8");  
            method.setHeader("Accept", "application/json"); 
            logger.warn("请求参数{}", parameters);
            method.setEntity(new StringEntity(parameters, Charset.forName("UTF-8")));  
            HttpResponse response = httpClient.execute(method);               
            int statusCode = response.getStatusLine().getStatusCode();  
            if (statusCode != HttpStatus.SC_OK) {  
                logger.error("Method failed:" + response.getStatusLine()); 
            }  

            // Read the response body  
            String resultString = EntityUtils.toString(response.getEntity());
            result = JSON.parseObject(resultString);
			
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
		
		if (result.getString("rspCode") != null)
		{
			if ("P00000".equals(result.getString("rspCode")))
			{
				logger.info("orderId:{},订购成功", order.getId());
				JSONObject objson = JSON.parseObject(result.get("data").toString());
				String rechargId = objson.getString("orderId");
				if (StringUtils.isEmpty(rechargId))
				{
					logger.warn("zhuowang充值返回rechargeId为空");
					throw new ServiceException(ResultCode.FAILED);
				}
				abstractOrderService.setRechargeId(order.getId(), rechargId, "feiyin");
			}
			else
			{
				String failedReason = result.getString("rspMsg");
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
		map.put("orderId", order.getRechargeId());//要查询的订单ID，此ID为掌上流量平台充值登记成功后返回的订单ID
		map.put("transId", order.getId());//transId	每次交互的唯一标示，不能重复	String	50	必填	交易流水号，用于确保唯一性，生成方式可参见附录。
		map.put("userCode", "jiexint");//userCode	用户代码	String	20	必填	由流量平台分配
		String signature = "";
		try
		{
			signature = SignatureUtils.getSignatureFeiyin(map);
		}
		catch (IOException e)
		{
			logger.warn("签名异常{}", e);
			throw new ServiceException(ResultCode.FAILED);
		}
		map.put("sign", signature);

		StringBuilder sb = new StringBuilder();
		sb.append(QUERY_SERVER_URL).append("?").append("orderId=").append(order.getRechargeId()).append("&trans_id=").append(map.get("trans_id"))
		.append("&userCode=").append(map.get("userCode")).append("&sign=").append(map.get("sign"));
		try
		{
			CloseableHttpClient httpClient = HttpClients.custom().build();
			HttpGet httpGet = new HttpGet(sb.toString());
			HttpResponse response = httpClient.execute(httpGet);
			HttpEntity entity = response.getEntity();
			if (entity != null)
			{
				String resutString = EntityUtils.toString(entity);
				logger.info("feiyin 充值查询返回结果{}", resutString);
				result = JSON.parseObject(resutString);
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

		if (result != null && "P00000".equals(result.getString("rspCode")))
		{
			if ("4".equals(result.getJSONArray("orderList").getJSONObject(0).getString("orderStatus")))
			{
				logger.info("orderId：{}订购成功", order.getId());
				rechargeService.rechargeSuccess(order.getId(), order.getUsername(), order.getNotifyUrl(), "Y", "订购成功", order.getExternalId());
			}
			else if ("1".equals(result.getJSONArray("orderList").getJSONObject(0).getString("orderStatus")))
			{
				// do nothing
			}
			else if ("5".equals(result.getJSONArray("orderList").getJSONObject(0).getString("orderStatus")))
			{
				logger.info("orderId：{}订购失败", order.getId());

				String failReson = "";
				try
				{
					failReson = result.getJSONArray("orderList").getJSONObject(0).getString("orderRspMsg");
				}
				catch (Exception e)
				{
					logger.warn("orderId：{}获取失败原因元素不存在", order.getId());
				}
//				rechargeService.rechargeSuccess(order.getId(), order.getUsername(), order.getNotifyUrl(), "N", failReson, order.getExternalId());
				rechargeService.rechargeFailed(order.getId(), order.getUsername(), order.getNotifyUrl(),failReson, order.getExternalId());
			}else{
				//1、2、3什么都不做
				
//				4.2订单状态码说明
//				1：订单提交成功；
//				2：待充值；
//				3：已提交，等待处理结果；
//				4：充值成功；
//				5：充值失败；
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
