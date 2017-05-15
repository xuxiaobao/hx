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
import com.miaosu.service.recharge.sup.XunfangzhouRecharge;

import com.alibaba.fastjson.JSONArray;

/**
 * 通知处理
 */
@Controller
@RequestMapping("/notify/xunfangzhou")
public class XunfangzhouNotifyController
{

	private static final Logger notifyLog = LoggerFactory.getLogger("notify");

	@Autowired
	private XunfangzhouRecharge xunfangzhouRecharge;

	@RequestMapping(value = "/orderstatus", method = { RequestMethod.GET, RequestMethod.POST })
	public void orderStatus(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		String jsonString = IOUtils.toString(request.getInputStream());
		notifyLog.info("xunfangzhou充值结果通知：{}", jsonString);

		JSONObject json = JSONObject.parseObject(jsonString);
		String arrayString = json.getJSONArray("resList").toJSONString();
		JSONArray array = JSONArray.parseArray(arrayString); 
		for(int i = 0; i < array.size(); i++){ 
			JSONObject jsonObject = array.getJSONObject(i);  
			String orderId = jsonObject.getString("orderNo");
			String orderStatus = jsonObject.getString("resCode");
			RechargeResult rechargeResult = new RechargeResult();
			rechargeResult.setOrderId(orderId);
			rechargeResult.setCode(orderStatus.equals("0") ? "Y" : "N");

			xunfangzhouRecharge.callBack(rechargeResult);
		} 

		

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