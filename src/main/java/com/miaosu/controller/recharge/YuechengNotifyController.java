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
import com.miaosu.service.recharge.sup.YuechengRecharge;

/**
 * 通知处理
 */
@Controller
@RequestMapping("/notify/yuecheng")
public class YuechengNotifyController
{

	private static final Logger notifyLog = LoggerFactory.getLogger("notify");

	@Autowired
	private YuechengRecharge yuechengRecharge;

	@RequestMapping(value = "/orderstatus", method = { RequestMethod.GET, RequestMethod.POST })
	public void orderStatus(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		/*String jsonString = IOUtils.toString(request.getInputStream());
		notifyLog.info("yuecheng充值结果通知：{}", jsonString);

		JSONObject json = JSONObject.parseObject(jsonString);

		String orderId = json.getString("channel_order_id");
		String orderStatus = json.getString("order_status");*/

		notifyLog.info("yuecheng充值结果通知：{}", request.getParameter("message"));
		
		String orderId = request.getParameter("orderno");
		String orderStatus = request.getParameter("code");
		
		RechargeResult rechargeResult = new RechargeResult();
		rechargeResult.setOrderId(orderId);
		rechargeResult.setCode(orderStatus.equals("4") ? "Y" : "N");

		yuechengRecharge.callBack(rechargeResult);
	}
}