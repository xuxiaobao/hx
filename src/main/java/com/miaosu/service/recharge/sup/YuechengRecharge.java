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
public class YuechengRecharge extends AbstractRecharge
{

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Value("${zhuowang.channel}")
	private String channelId = "1000264";

	@Value("${zhuowang.password}")
	private String password = "9eh1@1%wnv67$wlv";

//	@Value("${zhuowang.server}")
	private String SERVER_URL = "http://bcp.pro-group.cn/api/CallApi/Index";
		
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
		
		/*Action 	命令 	charge
		V 	版本号 	1.2 固定值
		Range 	流量类型 	0 全国流量 1省内流量，不带改参数时默认为0
		Account 	帐号 (签名) 	代理商编号(非平台登入账号)
		Mobile 	号码 (签名) 	充值手机号
		Package 	套餐 (签名) 	流量包大小(必须在getPackage返回流量包选择内)
		OrderNo 	订单号(签名) 	用户自定义订单号，1-32位字符
		TimeStamp 	时间戳(签名) 	时间戳格式:yyyyMMddHHmmss
		Sign 	签名 	参见签名算法*/

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
        logger.info("yuecheng 充值的流量{}", flowValue);
		map.put("account", "D145");//Account 	帐号 (签名) 	代理商编号(非平台登入账号)
		map.put("mobile", order.getPhone());//Mobile 	号码 (签名) 	充值手机号
		map.put("package", flowValue);//套餐 (签名) 	流量包大小(必须在getPackage返回流量包选择内)
		map.put("orderno", order.getId());//OrderNo 	订单号(签名) 	用户自定义订单号，1-32位字符
		
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");//设置日期格式
		String timeStamp = df.format(new Date());// new Date()为获取当前系统时间
		map.put("timestamp", timeStamp);//TimeStamp 	时间戳(签名) 	时间戳格式:yyyyMMddHHmmss	
//		map.put("key", "0da7e2c41773078e0ee37ade52889885");
		String signature = "";
		try
		{
			signature = SignatureUtils.getSignatureYuecheng(map);
		}
		catch (IOException e)
		{
			logger.warn("签名异常{}", e);
			throw new ServiceException(ResultCode.FAILED);
		}
		map.put("sign", signature);//Sign 	签名 	参见签名算法
		//不参与签名的
		map.put("action", "charge");//Action 	命令 	charge
		map.put("v", "1.2");//V 	版本号 	1.2 固定值
		map.put("range", range);//Range 	流量类型 	0 全国流量 1省内流量，不带改参数时默认为0
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
				String resultString = EntityUtils.toString(entity);
				logger.info("yuecheng 充值同步返回结果{}", resultString);
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

		if (result.getString("Code") != null)
		{
			if ("0".equals(result.getString("Code")))
			{
				logger.info("orderId:{},订购成功", order.getId());

				/*String rechargId = result.getJSONObject("content").getString("order_id");
				if (StringUtils.isEmpty(rechargId))
				{
					logger.warn("zhuowang充值返回rechargeId为空");
					throw new ServiceException(ResultCode.FAILED);
				}*/
				abstractOrderService.setRechargeId(order.getId(), "", "yuecheng");
			}
			else
			{
				String failedReason = result.getString("Message");
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
		map.put("account", "D145");//Account 	帐号 (签名) 	代理商编号(非平台登入账号)
		map.put("orderno", order.getId());//OrderNo 	订单号(签名) 	用户自定义订单号，1-32位字符
		map.put("count", "1");//一次取数量(签名)
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");//设置日期格式
		String timeStamp = df.format(new Date());// new Date()为获取当前系统时间
		map.put("timestamp", timeStamp);//TimeStamp 	时间戳(签名) 	时间戳格式:yyyyMMddHHmmss	
//		map.put("key", "0da7e2c41773078e0ee37ade52889885");
		String signature = "";
		try
		{
			signature = SignatureUtils.getSignatureYuecheng(map);
		}
		catch (IOException e)
		{
			logger.warn("签名异常{}", e);
			throw new ServiceException(ResultCode.FAILED);
		}
		map.put("sign", signature);//Sign 	签名 	参见签名算法
		//不参与签名的
		map.put("action", "charge");//Action 	命令 	charge
		map.put("v", "1.2");//V 	版本号 	1.2 固定值

		StringBuilder sb = new StringBuilder();
		sb.append(SERVER_URL).append("?").append("v=").append(map.get("v"))
		.append("&action=").append(map.get("action"))
		.append("&account=").append(map.get("account"))
		.append("&orderno=").append(map.get("orderno"))
		.append("&count=").append(map.get("count"))
		.append("&timestamp=").append(map.get("timestamp"))
		.append("&sign=").append(map.get("sign"));
		try
		{
			CloseableHttpClient httpClient = HttpClients.custom().build();
			HttpGet httpGet = new HttpGet(sb.toString());
			HttpResponse response = httpClient.execute(httpGet);
			HttpEntity entity = response.getEntity();
			if (entity != null)
			{
				String resutString = EntityUtils.toString(entity);
				logger.info("yuecheng 充值查询返回结果{}", resutString);
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

		if (result != null && "0000".equals(result.getString("code")))
		{
			
			if ("4".equals(result.getJSONArray("reports").getJSONObject(0).getString("status")))
			{
				logger.info("orderId：{}订购成功", order.getId());
				rechargeService.rechargeSuccess(order.getId(), order.getUsername(), order.getNotifyUrl(), "Y", "订购成功", order.getExternalId());
			}
			else if ("5".equals(result.getJSONArray("reports").getJSONObject(0).getString("status")))
			{
				logger.info("orderId：{}订购失败", order.getId());

				String failReson = "";
				try
				{
					failReson = result.getJSONArray("reports").getJSONObject(0).getString("reportcode");
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
