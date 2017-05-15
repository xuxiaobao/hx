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
import com.miaosu.util.RSAUtil;
import com.miaosu.util.RSAUtilTaiyong;
import org.apache.http.impl.client.DefaultHttpClient;
/**
 * 类的描述
 * 
 * @author wx
 * @Time 2016/2/18
 */
@Service
public class TaiyongRecharge extends AbstractRecharge
{

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Value("${zhuowang.channel}")
	private String channelId = "1000264";

	@Value("${zhuowang.password}")
	private String password = "9eh1@1%wnv67$wlv";

//	@Value("${zhuowang.server}")
	private String SERVER_URL = "http://115.28.101.43:40004/buyQuota";

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
		
		
		Product product = productService.get(order.getProductId());
		List<ProductDetail> detailList = product.getProductDetailList();
		String rangeStr = "";
		String range = "0";
		String flowValue = "";
        for(ProductDetail detail : detailList)
        {
        	//流量类型
        	if("2".equals(detail.getProId()))
        	{
        		rangeStr = detail.getProValue();
        		if(rangeStr.equals("省内")){
        			range = "province";
        		}else{
        			range = "nation";
        		}
        	}
        	//流量值
        	if("5".equals(detail.getProId()))
        	{
        		flowValue =  detail.getProValue();
        	}
        }
        logger.info("taiyong 充值的流量{}", flowValue);
        logger.info("客户订单号======{}", order.getId());
		
		map.put("appId", "24");//商户ID
		map.put("customerOrderId", order.getId());//客户订单号
		map.put("phoneNo", order.getPhone());//手机号码
		map.put("spec", flowValue);//流量规格
		map.put("scope", range);//使用范围（全国：nation，省内：province）
		map.put("callbackUrl", "http://123.56.203.67/hongxin/notify/taiyong/orderstatus");//回调地址
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmssSSS");//设置日期格式
		String timeStamp = df.format(new Date());// new Date()为获取当前系统时间
		map.put("timeStamp", timeStamp);//时间戳，格式yyyyMMddHHmmssSSS
		

		String signature = "";
		try
		{
			signature = RSAUtilTaiyong.getSignatureTaiyong(map);
		}
		catch (Exception e)
		{
			logger.warn("签名异常{}", e);
			throw new ServiceException(ResultCode.FAILED);
		}
		map.put("signature", signature);
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
			//				logger.info("taiyong 充值同步返回结果{}", resultString);
			//				result = JSON.parseObject(resultString);
			//			}

			// 1,拼接GET请求 url地址 real_path?uname=kate&pwd=123
			StringBuilder sb = null;
			sb = new StringBuilder(SERVER_URL);
			sb.append("?");
			for (String key : map.keySet()) {
				// uname=xiaolan& pwd=123&
				logger.info("===map==={}", "====map==");
				sb.append(key + "=" + map.get(key) + "&");
			}
			// 去掉最后的&
			sb.deleteCharAt(sb.length() - 1);

			logger.info("sb.toString()======{}", sb.toString());
			//	      创建HttpGet或HttpPost对象，将要请求的URL通过构造方法传入HttpGet或HttpPost对象。  
			HttpGet httpRequst = new HttpGet(sb.toString());  
			
			//	      new DefaultHttpClient().execute(HttpUriRequst requst);  
			//使用DefaultHttpClient类的execute方法发送HTTP GET请求，并返回HttpResponse对象。  
			HttpResponse httpResponse = new DefaultHttpClient().execute(httpRequst);//其中HttpGet是HttpUriRequst的子类  
			if(httpResponse.getStatusLine().getStatusCode() == 200)  
			{  
				HttpEntity httpEntity = httpResponse.getEntity();  
				String resultString = EntityUtils.toString(httpEntity);//取出应答字符串  
				// 一般来说都要删除多余的字符   
				resultString.replaceAll("\r", "");//去掉返回结果中的"\r"字符，否则会在结果字符串后面显示一个小方格  
				logger.info("taiyong 充值同步返回结果{}", resultString);
				result = JSON.parseObject(resultString);
			}else{
				logger.info("httpResponse.getStatusLine().getStatusCode(){}", httpResponse.getStatusLine().getStatusCode());
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

		if (result.getString("code") != null)
		{
			if ("0".equals(result.getString("code")))
			{
				logger.info("orderId:{},订购成功", order.getId());

//				String rechargId = result.getJSONObject("content").getString("order_id");
//				if (StringUtils.isEmpty(rechargId))
//				{
//					logger.warn("zhuowang充值返回rechargeId为空");
//					throw new ServiceException(ResultCode.FAILED);
//				}
				abstractOrderService.setRechargeId(order.getId(), "", "taiyong");
			}
			else
			{
				String failedReason = result.getString("code") + ":"
						+ result.getString("desc");
				// 订购失败
				logger.info("{}充值失败，失败原因{}", order.getId(), failedReason);
				rechargeService.rechargeSuccess(order.getId(), order.getUsername(), order.getNotifyUrl(), "N", failedReason, order.getExternalId());
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
		map.put("channel_order_ids", order.getId());
		map.put("channel_id", channelId);
		map.put("trans_id", UUID.randomUUID().toString().toLowerCase().replace("-", ""));
		String sign = "";
		try
		{
			sign = SignatureUtils.getSignature(map, password);
		}
		catch (IOException e)
		{
			logger.warn("签名异常{}", e);
			throw new ServiceException(ResultCode.FAILED);
		}
		map.put("sign", sign);

		StringBuilder sb = new StringBuilder();
		sb.append(SERVER_URL).append("rest/query?").append("channel_order_ids=").append(order.getId()).append("&channel_id=")
				.append(channelId).append("&trans_id=").append(map.get("trans_id")).append("&sign=").append(map.get("sign"));
		try
		{
			CloseableHttpClient httpClient = HttpClients.custom().build();
			HttpGet httpGet = new HttpGet(sb.toString());
			HttpResponse response = httpClient.execute(httpGet);
			HttpEntity entity = response.getEntity();
			if (entity != null)
			{
				String resutString = EntityUtils.toString(entity);
				logger.info("zhuowang 充值查询返回结果{}", resutString);
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

		if (result != null && "0000".equals(result.getJSONObject("response").getString("rspCode")))
		{
			if ("5".equals(result.getJSONArray("content").getJSONObject(0).getString("order_status")))
			{
				logger.info("orderId：{}订购成功", order.getId());
				rechargeService.rechargeSuccess(order.getId(), order.getUsername(), order.getNotifyUrl(), "Y", "订购成功", order.getExternalId());
			}
			else if ("4".equals(result.getJSONArray("content").getJSONObject(0).getString("order_status")))
			{
				// do nothing
			}
			else
			{
				logger.info("orderId：{}订购失败", order.getId());

				String failReson = "";
				try
				{
					failReson = result.getJSONArray("content").getJSONObject(0).getString("fail_reason");
				}
				catch (Exception e)
				{
					logger.warn("orderId：{}获取失败原因元素不存在", order.getId());
				}
				rechargeService.rechargeSuccess(order.getId(), order.getUsername(), order.getNotifyUrl(), "N", failReson, order.getExternalId());
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
}
