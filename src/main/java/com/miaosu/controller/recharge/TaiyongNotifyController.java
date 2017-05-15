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
import com.miaosu.service.recharge.sup.ZhuowangRecharge;
import com.miaosu.service.recharge.sup.TaiyongRecharge;
/**
 * 通知处理
 */
@Controller
@RequestMapping("/notify/taiyong")
public class TaiyongNotifyController
{

	private static final Logger notifyLog = LoggerFactory.getLogger("notify");

	@Autowired
	private TaiyongRecharge taiyongRecharge;

	@RequestMapping(value = "/orderstatus", method = { RequestMethod.GET, RequestMethod.POST })
	public void orderStatus(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		String jsonString = IOUtils.toString(request.getInputStream());
		notifyLog.info("taiyong充值结果通知：{}", jsonString);
//
//		JSONObject json = JSONObject.parseObject(jsonString);
//
//		String orderId = json.getString("customerOrderId");
//		String orderStatus = json.getString("status");
		
		String orderId = request.getParameter("customerOrderId");
		String orderStatus = request.getParameter("status");

		RechargeResult rechargeResult = new RechargeResult();
		rechargeResult.setOrderId(orderId);
		rechargeResult.setCode(orderStatus.equals("success") ? "Y" : "N");

		taiyongRecharge.callBack(rechargeResult);
	}
}