package com.miaosu.service.recharge.sup;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import java.util.UUID;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
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
import com.miaosu.service.recharge.sup.ECXmlRequest;
import com.miaosu.service.recharge.sup.ECXmlRequest.BIPTypes;
import com.miaosu.service.recharge.sup.ECXmlRequest.RoutingInfos;
import com.miaosu.service.recharge.sup.ECXmlRequest.Routings;
import com.miaosu.service.recharge.sup.ECXmlRequest.TransInfos;
import com.miaosu.base.ResultCode;
import com.miaosu.base.ServiceException;
import com.miaosu.model.Order;
import com.miaosu.model.enums.RechargeState;
import com.miaosu.service.orders.AbstractOrderService;
import com.miaosu.service.recharge.AbstractRecharge;
import com.miaosu.service.recharge.RechargeResult;
import com.miaosu.service.recharge.RechargeService;
import com.miaosu.util.SignatureUtils;


import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import com.miaosu.service.products.ProductService;
import com.miaosu.model.Product;
import com.miaosu.model.ProductDetail;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.XmlFriendlyNameCoder;
import com.thoughtworks.xstream.io.xml.XppDriver;

import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Document;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.InputStreamReader;  
import java.io.OutputStreamWriter;
import java.net.URLConnection; 
import com.miaosu.util.MD5Util;

//import org.apache.http.entity.mime.content.StringBody;
//import org.apache.http.entity.mime.MultipartEntityBuilder;

/**
 * 类的描述
 * 
 * @author wx
 * @Time 2016/2/18
 */
@Service
public class NeimengyidongRecharge extends AbstractRecharge
{

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Value("${zhuowang.channel}")
	private String channelId = "1000264";

	@Value("${zhuowang.password}")
	private String password = "9eh1@1%wnv67$wlv";

//	测试环境===
//	private String SERVER_URL = "http://111.13.21.128:80/ec_serv_intf/forec";
//	测试新地址	
	private String SERVER_URL = "http://221.179.195.107:20080/ec_serv_intf/forec";
	
//  3.生产环境	
//	private String SERVER_URL = "http://111.13.21.129:80/ec_serv_intf/forec";
	
	private String APP_KEY="HONGXINJCXXJS@NM";
	
	//测试环境===
//	private String APP_SECRET="rUihEFM87Q8UCzzpX2PWysqk";
	
	//4.生产环境	
	private String APP_SECRET="GJbhTXfIRwUcf2O9i3dQr4kH";
		
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

//		Map<String, String> map = new HashMap<String, String>();
//		
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
//		map.put("account", "D145");//Account 	帐号 (签名) 	代理商编号(非平台登入账号)
//		map.put("mobile", order.getPhone());//Mobile 	号码 (签名) 	充值手机号
//		map.put("package", flowValue);//套餐 (签名) 	流量包大小(必须在getPackage返回流量包选择内)
//		map.put("orderno", order.getId());//OrderNo 	订单号(签名) 	用户自定义订单号，1-32位字符
//		
//		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");//设置日期格式
//		String timeStamp = df.format(new Date());// new Date()为获取当前系统时间
//		map.put("timestamp", timeStamp);//TimeStamp 	时间戳(签名) 	时间戳格式:yyyyMMddHHmmss	
////		map.put("key", "0da7e2c41773078e0ee37ade52889885");
//		String signature = "";
//		try
//		{
//			signature = SignatureUtils.getSignatureYuecheng(map);
//		}
//		catch (IOException e)
//		{
//			logger.warn("签名异常{}", e);
//			throw new ServiceException(ResultCode.FAILED);
//		}
//		map.put("sign", signature);//Sign 	签名 	参见签名算法
//		//不参与签名的
//		map.put("action", "charge");//Action 	命令 	charge
//		map.put("v", "1.2");//V 	版本号 	1.2 固定值
//		map.put("range", range);//Range 	流量类型 	0 全国流量 1省内流量，不带改参数时默认为0
		//map.put("callback_url", "http://123.56.193.64/hongxin/notify/zhuowang/orderstatus");
//		JSONObject result = null;
		ECXmlRespose eCXmlRespose =null;
		OutputStreamWriter out=null;    
        BufferedReader in=null;


    	HttpPost httppost=new HttpPost(SERVER_URL);
    	
    	String bipCode = "BIP4B876";
    	String activityCode = "T4011137";
    	
    	//1.生产环境和测试环境相同
    	String PRODUCT_ID = "9003674668";
    	
    	String phoneNo = order.getPhone();
    	String pkgNo =order.getProductId();
    	String sessionID =order.getId();
    	
    	setAuthHeaders(httppost,APP_KEY,APP_SECRET,MD5Util.computeMD5(PRODUCT_ID),activityCode);
//    	String msgHeaer = createReqHeader(bipCode, activityCode);
    	StringBuilder sb = new StringBuilder();
    	sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
    	sb.append("<AdditionInfo>");
    	sb.append("<ProductID>");
    	sb.append(PRODUCT_ID);
    	sb.append("</ProductID>\n");
    	sb.append("<UserData>");
    	sb.append("<MobNum>");
    	sb.append(phoneNo);
    	sb.append("</MobNum>");
    	sb.append("<UserPackage>");
    	sb.append(pkgNo);
    	sb.append("</UserPackage>");  
    	sb.append("</UserData>\n");
    	sb.append("</AdditionInfo>");
    	String svcCont = sb.toString();

    	sb.setLength(0);
    	sb.append("<Version>0100</Version>");
    	//测试环境
//    	sb.append("<TestFlag>1</TestFlag>");
    	//2.生产环境
    	sb.append("<TestFlag>0</TestFlag>");
    	sb.append("<BIPType>");
    	sb.append("<BIPCode>");
    	sb.append(bipCode);
    	sb.append("</BIPCode>");
    	sb.append("<ActivityCode>");
    	sb.append(activityCode);
    	sb.append("</ActivityCode>");
    	sb.append("<ActionCode>0</ActionCode>");
    	sb.append("</BIPType>");
    	sb.append("<RoutingInfo>");
    	sb.append("<OrigDomain>DOMS</OrigDomain>");
    	sb.append("<RouteType>00</RouteType>");
    	sb.append("<Routing>");
    	sb.append("<HomeDomain>BBSS</HomeDomain>");
    	sb.append("<RouteValue>998</RouteValue>");
    	sb.append("</Routing>");
    	sb.append("</RoutingInfo>");

    	sb.append("<TransInfo>");
    	sb.append("<SessionID>");
    	sb.append(sessionID);
    	sb.append("</SessionID>");
    	SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");//设置日期格式
		String transIDOTime = df.format(new Date());// new Date()为获取当前系统时间
    	sb.append("<TransIDO>");
    	sb.append("BJHX"+transIDOTime+"000000000000");
    	sb.append("</TransIDO>");
    	sb.append("<TransIDOTime>");
    	
    	sb.append(transIDOTime);
    	sb.append("</TransIDOTime>");
    	sb.append("</TransInfo>\n");
    	String msgHeaer = sb.toString();
    	
    	
    	sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<InterBOSS>\n");
    	sb.append(msgHeaer);
    	sb.append("</InterBOSS>");
    	String xmlhead = sb.toString();
    	logger.info("neimengyidong 充值同步xmlhead{}", xmlhead);

    	sb.setLength(0);
    	sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<InterBOSS>\n");
    	sb.append("<SvcCont><![CDATA[");
    	sb.append(svcCont);
    	sb.append("]]></SvcCont>\n</InterBOSS>");
    	String xmlbody = sb.toString();
    	logger.info("neimengyidong 充值同步xmlbody{}", xmlbody);
    	sb.insert(0, '\n');
    	sb.insert(0, xmlhead);
    	StringBody body = new StringBody(xmlbody, ContentType.create("application/xml", Consts.UTF_8));
        StringBody head = new StringBody(xmlhead, ContentType.create("application/xml", Consts.UTF_8));
    	HttpEntity reqEntity = MultipartEntityBuilder.create().addPart("xmlhead", head)
                .addPart("xmlbody", body).build();

        httppost.setEntity(reqEntity);
        CloseableHttpClient httpclient = HttpClients.createDefault();
        
        
     // 4.3版本超时设置
     //   RequestConfig requestConfig = RequestConfig.custom()
        //        .setSocketTimeout(Integer.valueOf(parameters.getProperty("app.dpiTimeOut")))
        //        .setConnectTimeout(Integer.valueOf(parameters.getProperty("app.dpiTimeOut"))).build();// 设置请求和传输超时时间
        // httppost.setConfig(requestConfig);
        CloseableHttpResponse response = null;
        String bossRspStr="";
        try {
            response = httpclient.execute(httppost);
            System.out.println(response.getStatusLine());
            HttpEntity resEntity = response.getEntity();
            
            if (resEntity != null) {
                bossRspStr = EntityUtils.toString(resEntity, Consts.UTF_8);
                // 这是原始响应报文
                System.out.println("响应报文："+bossRspStr);
                logger.info("neimengyidong 充值同步响应报文{}", bossRspStr);
//                XStream xStream = new XStream(new XppDriver(new XmlFriendlyNameCoder("_-", "_")));//说明3(见文末)  
//                //将请求返回的内容通过xStream转换为UnifiedOrderRespose对象  
//                xStream.alias("InterBOSS", ECXmlRespose.class);  
//                eCXmlRespose = (ECXmlRespose) xStream.fromXML(bossRspStr);
            }
            EntityUtils.consume(resEntity);
			
		}
		catch (Exception ex)
		{
			logger.warn("订购失败,exMsg:{}; costTime:{}", ex.getMessage(), (System.currentTimeMillis() - begin));
			throw new ServiceException(ResultCode.FAILED);
		}
		finally
		{
			logger.info("订购结束; result:{}; costTime:{}", bossRspStr, (System.currentTimeMillis() - begin));
		}
		if (bossRspStr != null)
		{
			try {
				Document doc = DocumentHelper.parseText(bossRspStr); // 将字符串转为XML
		        Element rootElt = doc.getRootElement(); // 获取根节点
	//	        System.out.println("根节点：" + rootElt.getName()); // 拿到根节点的名称
		              
		        
		        String SvcCont = rootElt.elementTextTrim("SvcCont"); // 拿到根节点下的子节点SvcCont值
	//	        System.out.println("SvcCont:" + SvcCont);
		        
		        if(SvcCont==null){
		        	Element Response = rootElt.element("Response"); // 拿到根节点下的子节点Response值
		        	String RspCode = Response.elementTextTrim("RspCode");
//		        	System.out.println("RspCode"+RspCode);
		        	String RspDesc = Response.elementTextTrim("RspDesc"); // 拿到根节点下的子节点Status值
//		        	System.out.println("RspDesc"+RspDesc);
		        	if("0000".equals(RspCode)){
		        		
		        	}else{
		        		String failedReason = RspDesc;
//		        		System.out.println("failedReason"+failedReason);
		        		logger.info("{}充值失败，失败原因{}", order.getId(), failedReason);
		        		rechargeService.rechargeFailed(order.getId(), order.getUsername(), order.getNotifyUrl(),failedReason, order.getExternalId());
		        	}
		        }else{
		        	Document doc1 = DocumentHelper.parseText(SvcCont);// 将拿到值得字符串转为XML
			        Element rootElt1 = doc1.getRootElement(); // 获取值中根节点
		//	        System.out.println("根节点：" + rootElt1.getName()); // 拿到根节点的名称
			        
			        String Status = rootElt1.elementTextTrim("Status"); // 拿到根节点下的子节点Status值
		//	        System.out.println("Status:" + Status);
			        
			        String ErrDesc = rootElt1.elementTextTrim("ErrDesc"); // 拿到根节点下的子节点ErrDesc值
		//	        System.out.println("ErrDesc:" + ErrDesc);
			        
		
			        Iterator iter = rootElt1.elementIterator("OperSeqList"); // 获取根节点下的子节点OperSeqList
			        // 遍历head节点
			        String OperSeq ="";
			        while (iter.hasNext()) {
			            Element recordEle = (Element) iter.next();
			            OperSeq = recordEle.elementTextTrim("OperSeq"); // 拿到OperSeqList节点下的子节点OperSeq值
		//	            System.out.println("OperSeq:" + OperSeq);
			        }
					
					
					if ("00".equals(Status))
					{
						logger.info("orderId:{},订购成功", order.getId());
		
						/*String rechargId = result.getJSONObject("content").getString("order_id");
						if (StringUtils.isEmpty(rechargId))
						{
							logger.warn("zhuowang充值返回rechargeId为空");
							throw new ServiceException(ResultCode.FAILED);
						}*/
						String rechargId = OperSeq;
						if (StringUtils.isEmpty(rechargId))
						{
							logger.warn("neimengyidong充值返回rechargeId为空");
							throw new ServiceException(ResultCode.FAILED);
						}
						abstractOrderService.setRechargeId(order.getId(), rechargId, "neimengyidong");
					}
					else
					{
						String failedReason = "";
						if("01".equals(Status)){
							failedReason = "01-	ProductID未找到、已注销或者与发起方机构不匹配";
						}else if("02".equals(Status)){
							failedReason = "02-	ProductID指定的流量统付订购关系不是个人流量包模式，不支持个人叠加包订购";
						}else if("03".equals(Status)){
							failedReason = "03-	距离月底不足48小时，不允许再订购个人叠加包";
						}else if("04".equals(Status)){
							failedReason = "04-	无效成员，不能订购叠加包（保留错误代码，不再使用）";
						}else if("05".equals(Status)){
							failedReason = "05-	成员数量过多，超过50个限制";
						}else if("06".equals(Status)){
							failedReason = "06-	不能提交重复手机号";
						}else if("14".equals(Status)){
							failedReason = "主办省已经暂停此订购的成员和叠加包操作，请咨询省公司";
						}else if("99".equals(Status)){
							failedReason = ErrDesc;
						}
						// 订购失败
						logger.info("{}充值失败，失败原因{}", order.getId(), failedReason);
		//				rechargeService.rechargeSuccess(order.getId(), order.getUsername(), order.getNotifyUrl(), "N", failedReason, order.getExternalId());
						rechargeService.rechargeFailed(order.getId(), order.getUsername(), order.getNotifyUrl(),failedReason, order.getExternalId());
					}
		        }
		        
		        
			} catch (Exception e) {
		        e.printStackTrace();
	
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
//		JSONObject result = null;
//		long begin = System.currentTimeMillis();
//
//		HashMap<String, String> map = new HashMap<String, String>();
//		map.put("account", "D145");//Account 	帐号 (签名) 	代理商编号(非平台登入账号)
//		map.put("orderno", order.getId());//OrderNo 	订单号(签名) 	用户自定义订单号，1-32位字符
//		map.put("count", "1");//一次取数量(签名)
//		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");//设置日期格式
//		String timeStamp = df.format(new Date());// new Date()为获取当前系统时间
//		map.put("timestamp", timeStamp);//TimeStamp 	时间戳(签名) 	时间戳格式:yyyyMMddHHmmss	
////		map.put("key", "0da7e2c41773078e0ee37ade52889885");
//		String signature = "";
//		try
//		{
//			signature = SignatureUtils.getSignatureYuecheng(map);
//		}
//		catch (IOException e)
//		{
//			logger.warn("签名异常{}", e);
//			throw new ServiceException(ResultCode.FAILED);
//		}
//		map.put("sign", signature);//Sign 	签名 	参见签名算法
//		//不参与签名的
//		map.put("action", "charge");//Action 	命令 	charge
//		map.put("v", "1.2");//V 	版本号 	1.2 固定值
//
//		StringBuilder sb = new StringBuilder();
//		sb.append(SERVER_URL).append("?").append("v=").append(map.get("v"))
//		.append("&action=").append(map.get("action"))
//		.append("&account=").append(map.get("account"))
//		.append("&orderno=").append(map.get("orderno"))
//		.append("&count=").append(map.get("count"))
//		.append("&timestamp=").append(map.get("timestamp"))
//		.append("&sign=").append(map.get("sign"));
//		try
//		{
//			CloseableHttpClient httpClient = HttpClients.custom().build();
//			HttpGet httpGet = new HttpGet(sb.toString());
//			HttpResponse response = httpClient.execute(httpGet);
//			HttpEntity entity = response.getEntity();
//			if (entity != null)
//			{
//				String resutString = EntityUtils.toString(entity);
//				logger.info("yuecheng 充值查询返回结果{}", resutString);
//				result = JSON.parseObject(resutString);
//			}
//		}
//		catch (Exception ex)
//		{
//			logger.warn("查询订单失败, exMsg:{}; costTime:{}", ex.getMessage(), (System.currentTimeMillis() - begin));
//			throw new ServiceException(ResultCode.FAILED);
//		}
//		finally
//		{
//			logger.info("查询订单结束; result:{}; costTime:{}", result, (System.currentTimeMillis() - begin));
//		}
//
//		if (result != null && "0000".equals(result.getString("code")))
//		{
//			
//			if ("4".equals(result.getJSONArray("reports").getJSONObject(0).getString("status")))
//			{
//				logger.info("orderId：{}订购成功", order.getId());
//				rechargeService.rechargeSuccess(order.getId(), order.getUsername(), order.getNotifyUrl(), "Y", "订购成功", order.getExternalId());
//			}
//			else if ("5".equals(result.getJSONArray("reports").getJSONObject(0).getString("status")))
//			{
//				logger.info("orderId：{}订购失败", order.getId());
//
//				String failReson = "";
//				try
//				{
//					failReson = result.getJSONArray("reports").getJSONObject(0).getString("reportcode");
//				}
//				catch (Exception e)
//				{
//					logger.warn("orderId：{}获取失败原因元素不存在", order.getId());
//				}
////				rechargeService.rechargeSuccess(order.getId(), order.getUsername(), order.getNotifyUrl(), "N", failReson, order.getExternalId());
//				rechargeService.rechargeFailed(order.getId(), order.getUsername(), order.getNotifyUrl(),failReson, order.getExternalId());
//			}
//		}
//		else
//		{
//			logger.warn("订单查询返回结果为null");
//			throw new ServiceException(ResultCode.FAILED);
//		}
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
					if(rechargeResult.getCode().equals("N")){
						rechargeService.rechargeFailed(order.getId(), order.getUsername(), order.getNotifyUrl(),rechargeResult.getMsg(), order.getExternalId());
					}else{
						rechargeService.rechargeSuccess(order.getId(), order.getUsername(), order.getNotifyUrl(), rechargeResult.getCode(),
								rechargeResult.getMsg(), order.getExternalId());
					}
				}
			}
		}
		catch (Exception ex)
		{
			logger.warn("处理充值结果通知失败：{}， exMsg:{}", ex.getMessage());
		}
	}
	
    public static String createOrderInfo1(Order order) throws UnsupportedEncodingException {  
        //生成订单对象  
    	ECXmlRequest eCXmlRequest = new ECXmlRequest();  
    	eCXmlRequest.setVersion("0100");
    	eCXmlRequest.setTestFlag("1");
    	
    	BIPTypes bIPTypes= new BIPTypes();
    	bIPTypes.setBIPCode("BIP4B876");
    	bIPTypes.setActivityCode("T4011137");
    	bIPTypes.setActionCode("0");
    	eCXmlRequest.setBIPType(bIPTypes);
    	
    	RoutingInfos routingInfo = new RoutingInfos();
    	routingInfo.setOrigDomain("DOMS");
    	routingInfo.setRouteType("00");
    	Routings routing = new Routings();
    	routing.setHomeDomain("BBSS");
    	routing.setRouteValue("998");
    	routingInfo.setRouting(routing);
    	eCXmlRequest.setRoutingInfo(routingInfo);
    	
    	
    	TransInfos transInfo = new TransInfos();
    	//ssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss
    	transInfo.setSessionID(order.getId());
    	transInfo.setTransIDO(order.getId());
    	//ssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss
    	SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");//设置日期格式
		String transIDOTime = df.format(new Date());// new Date()为获取当前系统时间
    	transInfo.setTransIDOTime(transIDOTime);
    	eCXmlRequest.setTransInfo(transInfo);
    	String svcCont="<![CDATA[<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
    	+"<AdditionInfo><ProductID>9003674668</ProductID><UserData><MobNum>"+order.getPhone()+"</MobNum><UserPackage>"+order.getProductId()+"</UserPackage></UserData></AdditionInfo>]]>";
//    	eCXmlRequest.setSvcCont(svcCont);

        //将订单对象转为xml格式  
        XStream xStream = new XStream(new XppDriver(new XmlFriendlyNameCoder("_-", "_"))); //<span style="color:#ff0000;"><strong>说明3(见文末)</strong></span>  
//        XStream xStream   = new XStream(new DomDriver("UTF-8", new XmlFriendlyNameCoder("-_", "_")));
        xStream.alias("InterBOSS", ECXmlRequest.class);//根元素名需要是xml  
         
        String prePayXml = xStream.toXML(eCXmlRequest);  
//        try {
//			prePayXml = new String(prePayXml.getBytes(), "ISO-8859-1");
//        prePayXml = new String(prePayXml.toString().getBytes(), "utf-8");  
//		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} 
        prePayXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+prePayXml+"<?xml version=\"1.0\" encoding=\"UTF-8\"?><InterBOSS><SvcCont>"+svcCont+"</SvcCont></InterBOSS>";
        return prePayXml.trim();
    } 
    
    
    private String getXmlInfo(Order order) {  
    	
    	String svcCont="<![CDATA[<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
        	+"<AdditionInfo><ProductID>9003674668</ProductID><UserData><MobNum>"+order.getPhone()+"</MobNum><UserPackage>"+order.getProductId()+"</UserPackage></UserData></AdditionInfo>]]>";
    	
        StringBuilder sb = new StringBuilder();   
        sb.append("<?xmlversion='1.0'encoding='UTF-8'?><InterBOSS><Version>0100</Version><TestFlag>1</TestFlag>" +
        		"<BIPType><BIPCode>BIP4B876</BIPCode><ActivityCode>T4011137</ActivityCode><ActionCode>0</ActionCode></BIPType>" +
        		"<RoutingInfo><OrigDomain>DOMS</OrigDomain><RouteType>00</RouteType><Routing><HomeDomain>BBSS</HomeDomain><RouteValue>998</RouteValue></Routing></RoutingInfo>" +
        		"<TransInfo><SessionID>"+order.getId()+"</SessionID><TransIDO>"+order.getId()+"</TransIDO><TransIDOTime>20150206164851</TransIDOTime></TransInfo></InterBOSS>");  
        
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?><InterBOSS><SvcCont>"+svcCont+"</SvcCont></InterBOSS>");  
        return sb.toString();  
    } 
    
	public void setAuthHeaders(HttpPost httppost, String appKey, String appSecret, String product, String activicode)
	{
		String auth = "WSSE realm=\"DOMS\",profile=\"UsernameToken\",type=\"AppKey\"";
		httppost.setHeader("Authorization", auth);
		String nonce = genNonce();
		String created = genCreated();
		String pd = digistAppSecret(nonce, created, appSecret);
		String wsse = "UsernameToken Username=\"" + appKey + "\", PasswordDigest=\"" + pd + "\", Nonce=\"" + nonce + 
		"\", Created=\"" + created + "\"";
		System.out.println("PasswordDigest	：" + pd);
		System.out.println("created	：" + created);
		logger.info("neimengyidong 充值同步created{}", created);
		logger.info("neimengyidong 充值同步pd{}", pd);
		logger.info("neimengyidong 充值同步wsse{}", wsse);
		httppost.setHeader("X-WSSE", wsse);
		httppost.setHeader("PRODID", product);
		httppost.setHeader("ACTCODE", activicode);
	}

	public String genNonce1(){
		Random random=new Random();// 定义随机类
		int result=random.nextInt(10);// 返回[0,10)集合中的整数，注意不包括10
		String nonce = getBase64(result+"");
		return nonce;
	}
	
	  private String genNonce() {
		    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		    String timeStr = sdf.format(new Date());
		    String randomStr = genRandom(10000000, 99999999);
		    String str = timeStr.substring(0, 8) + randomStr + timeStr.substring(8);
		    char[] ca = str.toCharArray();

		    for (int i = 0; i < ca.length; i++) {
		      int ri = new Integer(genRandom(0, 21)).intValue();

		      ca[ri] = ca[i];
		    }

		    String res = new String(ca);
		    System.out.println("nonce	:	" + res);
		    return getBase64(res);
		  }
	  
	  private String genRandom(int min, int max) {
		    Random random = new Random();
		    int s = random.nextInt(max) % (max - min + 1) + min;
		    return new Integer(s).toString();
		  }

//	//将 s 进行 BASE64 编码 
//	public static String getBASE64(String s) { 
//		if (s == null) return null; 
//		return (new sun.misc.BASE64Encoder()).encode( s.getBytes() ); 
//	} 
//
//	// 将 BASE64 编码的字符串 s 进行解码 
//	public static String getFromBASE64(String s) { 
//		if (s == null) return null; 
//		BASE64Decoder decoder = new BASE64Decoder(); 
//		try { 
//			byte[] b = decoder.decodeBuffer(s); 
//			return new String(b); 
//		} catch (Exception e) { 
//			return null; 
//		} 
//	}
	
    public String getBase64(String str) {  
        byte[] b = null;  
        String s = null;  
        try {  
            b = str.getBytes("utf-8");  
        } catch (UnsupportedEncodingException e) {  
            e.printStackTrace();  
        }  
        if (b != null) {  
            s = new BASE64Encoder().encode(b);  
        }  
        return s;  
    }  
  
    // 解密  
    public String getFromBase64(String s) {  
        byte[] b = null;  
        String result = null;  
        if (s != null) {  
            BASE64Decoder decoder = new BASE64Decoder();  
            try {  
                b = decoder.decodeBuffer(s);  
                result = new String(b, "utf-8");  
            } catch (Exception e) {  
                e.printStackTrace();  
            }  
        }  
        return result;  
    }

	public String genCreated(){
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");//设置日期格式
		String transIDOTime = df.format(new Date());// new Date()为获取当前系统时间
		return transIDOTime;
	}
	public String digistAppSecret(String nonce, String created,String appSecret){
//		String passwordDigest = getBase64(Hex.encodeHexString(SHA256(nonce + created + appSecret)));
		String passwordDigest=getBase64(Hex.encodeHexString(SHA256(getFromBase64(nonce) + created + appSecret)));
		passwordDigest = passwordDigest.replaceAll("[\\s*\t\n\r]", ""); 
		return passwordDigest;
	}
	public byte[] SHA256(String t){
		byte[] barr = null;
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(t.getBytes("UTF-8"));
			barr = md.digest();		
		} catch (Exception e) {			
			e.printStackTrace();
		}
		return barr;
	}
	
}
