package com.miaosu.service.recharge.sup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
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
import com.miaosu.util.MD5Util;

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
public class LiudaoRecharge extends AbstractRecharge
{

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Value("${zhuowang.channel}")
	private String channelId = "1000264";

	@Value("${zhuowang.password}")
	private String password = "9eh1@1%wnv67$wlv";

//	@Value("${zhuowang.server}")
	private String SERVER_URL = "http://120.26.41.69/Flow/api/flow.do";

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
		
//		opcount	int	充值的流量(移动10 30 70 150 500 1024 联通 20 50 100 200 500电信 10 30 50 100 200 500 1024)
//		opmobile	string	充值的手机号码
//		notifyurl	string	回调地址
//		opid	string	注册时候的手机号码13466601196
//		targetcode	string	通道编码0102
//		sourceorder	string	对方订单号
//		sign	string	md5（targetcode=1212&notifyurl=http://www.baidu.com&opid=15333333333&opcount=10&opmobile=15333333333 &sourceorder=11111&key=md5(secretkey)）

		Map<String, String> map = new LinkedHashMap<String, String>();
		
		
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
        			range = "1";
        		}else{
        			range = "0";
        		}
        	}
        	//流量值
        	if("5".equals(detail.getProId()))
        	{
        		flowValue =  detail.getProValue();
        	}
        }
        logger.info("liudao 充值的流量{}", flowValue);
		map.put("targetcode", "0102");//targetcode	string	通道编码0102
		map.put("notifyurl", "http://123.56.203.67/hongxin/notify/liudao/orderstatus");//notifyurl	string	回调地址
		map.put("opid", "13466601196");//opid	string	注册时候的手机号码13466601196
		map.put("opcount", flowValue);//opcount	int	充值的流量(移动10 30 70 150 500 1024 联通 20 50 100 200 500电信 10 30 50 100 200 500 1024)
		map.put("opmobile", order.getPhone());//opmobile	string	充值的手机号码
		map.put("sourceorder", order.getId());//sourceorder	string	对方订单号
//		map.put("key", key);
//		map.put("key", "0da7e2c41773078e0ee37ade52889885");
		String signature = "";
		try
		{
			signature = SignatureUtils.getSignatureLiudao(map);
		}
		catch (IOException e)
		{
			logger.warn("签名异常{}", e);
			throw new ServiceException(ResultCode.FAILED);
		}
		map.put("sign", signature);//Sign 	签名 	参见签名算法
		//map.put("callback_url", "http://123.56.193.64/hongxin/notify/zhuowang/orderstatus");
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
				String resultString = EntityUtils.toString(entity,"utf-8");
				logger.info("liudao 充值同步返回结果{}", resultString);
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

		if (result.getString("response") != null)
		{
			if ("success".equals(result.getString("response")))
			{
				logger.info("orderId:{},订购成功", order.getId());

				/*String rechargId = result.getJSONObject("content").getString("order_id");
				if (StringUtils.isEmpty(rechargId))
				{
					logger.warn("zhuowang充值返回rechargeId为空");
					throw new ServiceException(ResultCode.FAILED);
				}*/
				abstractOrderService.setRechargeId(order.getId(), "", "liudao");
			}
			else
			{
				String failedReason = result.getString("msg");
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
