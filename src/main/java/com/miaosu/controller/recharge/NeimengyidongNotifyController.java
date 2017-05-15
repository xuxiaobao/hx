package com.miaosu.controller.recharge;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.fastjson.JSONObject;
import com.miaosu.service.recharge.RechargeResult;
import com.miaosu.service.recharge.sup.NeimengyidongRecharge;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.entity.mime.content.ContentBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.miaosu.service.orders.AbstractOrderService;
import org.dom4j.DocumentHelper;
import com.miaosu.model.Order;

/**
 * 通知处理
 */
@Controller
@RequestMapping("/notify/neimengyidong")
public class NeimengyidongNotifyController
{

	private static final Logger notifyLog = LoggerFactory.getLogger("notify");
	
    @Autowired
    private AbstractOrderService abstractOrderService;

	@Autowired
	private NeimengyidongRecharge neimengyidongRecharge;

	@RequestMapping(value = "/orderstatus", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public String orderStatus(HttpServletRequest request, HttpServletResponse response,String xmlhead,String xmlbody) throws IOException
	{
		/*String jsonString = IOUtils.toString(request.getInputStream());
		notifyLog.info("yuecheng充值结果通知：{}", jsonString);

		JSONObject json = JSONObject.parseObject(jsonString);

		String orderId = json.getString("channel_order_id");
		String orderStatus = json.getString("order_status");*/
		

		
		notifyLog.info("neimengyidong充值结果通知xmlhead{}", xmlhead);
		notifyLog.info("neimengyidong充值结果通知xmlbody{}", xmlbody);
		Order order =null;
		try{
			Document docXmlbody = DocumentHelper.parseText(xmlbody); // 将字符串转为XML
			Element rootElt = docXmlbody.getRootElement(); // 获取根节点

			String SvcCont = rootElt.elementTextTrim("SvcCont");

			Document doc1 = DocumentHelper.parseText(SvcCont);// 将拿到值得字符串转为XML
			Element rootElt1 = doc1.getRootElement(); // 获取值中根节点

			String OperSeq = rootElt1.elementTextTrim("OperSeq"); // 拿到根节点下的子节点OperSeq值

			//处理失败的记录总数	文件级错误填0，记录处理全部成功则此处填0
			String FailNum = rootElt1.elementTextTrim("FailNum"); // 拿到根节点下的子节点FailNum值

			String RspDesc="";
			if("0".equals(FailNum)){
				RspDesc="";
			}else{
				Element FailInfo = rootElt1.element("FailInfo");
				RspDesc = FailInfo.elementTextTrim("RspDesc");
//				System.out.println("RspDesc:"+RspDesc);
			}

			
			order = abstractOrderService.findByCreateTimeAfterAndRechargeId(new Date(System.currentTimeMillis() - 3 * 24 * 60 * 60 * 1000),OperSeq);

			notifyLog.info("neimengyidong充值结果通知OperSeq{}", OperSeq);
			notifyLog.info("neimengyidong充值结果通知FailNum{}", FailNum);
			String orderId = order.getId();
			String orderStatus = FailNum;


			RechargeResult rechargeResult = new RechargeResult();
			rechargeResult.setOrderId(orderId);
			rechargeResult.setCode(orderStatus.equals("0") ? "Y" : "N");
			rechargeResult.setMsg(RspDesc);
			neimengyidongRecharge.callBack(rechargeResult);

		} catch (Exception e) {
			e.printStackTrace();

		}
		StringBuilder sb = new StringBuilder();
		sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
		sb.append("<InterBOSS>");
		sb.append("<Version>0100</Version>");
		sb.append("<TestFlag>1</TestFlag>");
		sb.append("<BIPType>");
		sb.append("<BIPCode>BIP4B877</BIPCode>");
		sb.append("<ActivityCode>T4011138</ActivityCode>");
		sb.append("<ActionCode>1</ActionCode>");
		sb.append("</BIPType>");
		sb.append("<RoutingInfo>");
		sb.append("<OrigDomain>BBSS</OrigDomain>");
		sb.append("<RouteType>00</RouteType>");
		sb.append("<Routing>");
		sb.append("<HomeDomain>STKP</HomeDomain>");
		sb.append("<RouteValue>998</RouteValue>");
		sb.append("</Routing>");
		sb.append("</RoutingInfo>");
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");//设置日期格式
		String transIDOTime = df.format(new Date());// new Date()为获取当前系统时间
		sb.append("<TransInfo>");
		sb.append("<SessionID>"+order.getId()+"</SessionID>");
		sb.append("<TransIDO>"+"BJHX"+transIDOTime+"000000000000"+"</TransIDO>");
		sb.append("<TransIDOTime>"+transIDOTime+"</TransIDOTime>");
		sb.append("<TransIDH>"+"BJHX"+transIDOTime+"000000000000"+"</TransIDH>");
		sb.append("<TransIDHTime>"+transIDOTime+"</TransIDHTime>");
		sb.append("</TransInfo>");
		sb.append("<Response>");
		sb.append("<RspType>0</RspType>");
		sb.append("<RspCode>0000</RspCode>");
		sb.append("<RspDesc>Success</RspDesc>");
		sb.append("</Response>");
		sb.append("</InterBOSS>");
		
		
//		PrintWriter out = null;
//		try {
//			out = response.getWriter();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		out.println(sb.toString());
//		out.flush();
//		out.close();
		
		return sb.toString();

//		notifyLog.info("neimengyidong充值结果通知：{}", IOUtils.toString(request.getInputStream()));
//		notifyLog.info("neimengyidong充值结果通知：{}", request.getParameter("message"));
		
//		String orderId = request.getParameter("orderno");
//		String orderStatus = request.getParameter("code");
//		
//		RechargeResult rechargeResult = new RechargeResult();
//		rechargeResult.setOrderId(orderId);
//		rechargeResult.setCode(orderStatus.equals("4") ? "Y" : "N");
//
//		neimengyidongRecharge.callBack(rechargeResult);
	}
}