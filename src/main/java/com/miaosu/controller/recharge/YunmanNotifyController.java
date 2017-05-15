package com.miaosu.controller.recharge;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.fastjson.JSONObject;
import com.miaosu.service.recharge.RechargeResult;
import com.miaosu.service.recharge.sup.YunmanRecharge;
import com.alibaba.fastjson.JSONArray;

/**
 * 通知处理
 */
@Controller
@RequestMapping("/notify/yunman")
public class YunmanNotifyController
{

	private static final Logger notifyLog = LoggerFactory.getLogger("notify");

	@Autowired
	private YunmanRecharge yunmanRecharge;

	@RequestMapping(value = "/orderstatus", method = { RequestMethod.GET, RequestMethod.POST })
	public String orderStatus(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		String jsonString = IOUtils.toString(request.getInputStream());
		notifyLog.info("yunman充值结果通知：{}", jsonString);
		
//		String param = request.getParameter("param");
//		notifyLog.info("yunman充值结果通知param：{}", param);
		if(jsonString.contains("=")){
			jsonString = jsonString.split("=")[1];
		}

		JSONObject json = JSONObject.parseObject(jsonString);
		String data = json.getString("data");
		JSONObject jsonData = JSONObject.parseObject(data);
		String arrayString = jsonData.getJSONArray("messageList").toJSONString();
		JSONArray array = JSONArray.parseArray(arrayString); 
		for(int i = 0; i < array.size(); i++){ 
			JSONObject jsonObject = array.getJSONObject(i);  
			String orderId = jsonObject.getString("requestid");
			String orderStatus = jsonObject.getString("state");
			String statedes = jsonObject.getString("statedes");
			RechargeResult rechargeResult = new RechargeResult();
			rechargeResult.setOrderId(orderId);
			rechargeResult.setCode(orderStatus.equals("0") ? "Y" : "N");
			rechargeResult.setMsg(statedes);
			yunmanRecharge.callBack(rechargeResult);
		}
		return "ok";

//		String orderId = json.getString("channel_order_id");
//		String orderStatus = json.getString("order_status");

//		notifyLog.info("yuecheng充值结果通知：{}", request.getParameter("message"));
//		
//		String orderId = request.getParameter("orderno");
//		String orderStatus = request.getParameter("code");
		
//		RechargeResult rechargeResult = new RechargeResult();
//		rechargeResult.setOrderId(orderId);
//		rechargeResult.setCode(orderStatus.equals("4") ? "Y" : "N");
//
//		yuechengRecharge.callBack(rechargeResult);
	}
}