package com.miaosu.service.recharge.sup;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.miaosu.base.ResultCode;
import com.miaosu.base.ServiceException;
import com.miaosu.model.Order;
import com.miaosu.model.Product;
import com.miaosu.model.ProductDetail;
import com.miaosu.model.enums.RechargeState;
import com.miaosu.service.orders.AbstractOrderService;
import com.miaosu.service.products.ProductService;
import com.miaosu.service.recharge.AbstractRecharge;
import com.miaosu.service.recharge.RechargeResult;
import com.miaosu.service.recharge.RechargeService;
import com.miaosu.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
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
import sun.misc.BASE64Encoder;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.Map.Entry;

/**
 * 类的描述
 *
 * @author wx
 * @Time 2016/2/18
 */
@Service
public class YunmanRecharge extends AbstractRecharge {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${zhuowang.channel}")
    private String channelId = "1000264";

    @Value("${zhuowang.password}")
    private String password = "9eh1@1%wnv67$wlv";

    @Value("${yunMan.or.hongXin.partyId}")
    String partyId;
    @Value("${yunMan.or.hongXin.key}")
    String key;


    private String SERVER_URL = "http://114.55.79.251:28070/group_qw_server/submit";
    private String QUERY_SERVER_URL = "http://114.55.79.251:28070/group_qw_server/querystatus";

    @Autowired
    private AbstractOrderService abstractOrderService;

    @Autowired
    private RechargeService rechargeService;

    @Autowired
    private ProductService productService;


    @Override
    public void recharge(Order order) {

        logger.info("开始进行充值：{}", order.getId());
        long begin = System.currentTimeMillis();
        Map<String, String> map = new HashMap<String, String>();
        /**
         * 要构建的查询参数
         * param={
         * "header":{
         * "sign":"2a0a8042bd7efd4f2a40a070ad8fb929",
         * "partyId":10002015
         * "report_url":"http://www.xxxx.com/callback"
         * },
         * "body":{
         * "userdataList":[{
         * "userPackage":"10",
         * "mobiles":"13910567310"
         * }],
         * "size":1,
         * "type":"0",
         * "requestid":"2016093009595610002015518203"
         * }
         * }
         */

        String requestid = order.getId();//
        String body = "";
        JSONObject jUserdataList = new JSONObject();

        Product product = productService.get(order.getProductId());
        List<ProductDetail> detailList = product.getProductDetailList();

        /**
         * 以前的无用逻辑
         * String rangeStr = "";
         * String range = "0";
         *
         *  for(ProductDetail detail : detailList)
         * {
         *流量类型
         *if("2".equals(detail.getProId()))
         *{
         *rangeStr = detail.getProValue();
         *if(rangeStr.equals("省内")){
         *range = "1";
         *}else{
         *range = "0";
         *}
         *}
         *流量值
         *if("5".equals(detail.getProId()))
         *{
         *flowValue =  detail.getProValue();
         *}
         *}
         *
         */
        String flowValue = "";
        for (ProductDetail detail : detailList) {
            //流量值
            if ("5".equals(detail.getProId())) {
                flowValue = detail.getProValue();
            }
        }

        jUserdataList.put("userPackage", flowValue);//
        jUserdataList.put("mobiles", order.getPhone());//

        JSONArray arryUserdataList = new JSONArray();
        arryUserdataList.add(jUserdataList);
        JSONObject jBody = new JSONObject(true);
        jBody.put("userdataList", arryUserdataList);
        jBody.put("size", 1);
        jBody.put("type", "0");
        jBody.put("requestid", requestid);//
        body = jBody.toString();
        logger.info("body：{}", jBody.toString());

        //MD5(body+json中整个body部分+key+ key+partyId+partyId +requestid+requestid)
        String sign = MD5Util.computeMD5("body" + body + "key" + key + "partyId" + partyId + "requestid" + requestid);
        //String sign = EncoderByMd5("body"+body+"key"+key+"partyId"+partyId+"requestid"+requestid);

        JSONObject jHeader = new JSONObject();
        jHeader.put("sign", sign);
        jHeader.put("partyId", partyId);
        jHeader.put("report_url", "http://123.56.193.64/hongxin/notify/yunman/orderstatus");

        logger.info("header：{}", jHeader.toString());

        String paramStr = "";
        JSONObject jParam = new JSONObject();
        jParam.put("header", jHeader);
        jParam.put("body", jBody);
        paramStr = jParam.toString();
        logger.info("paramStr：{}", paramStr);

        map.put("param", paramStr);

        JSONObject result = null;
        try {
            CloseableHttpClient httpClient = HttpClients.custom().build();
            HttpPost httpPost = new HttpPost(SERVER_URL);

            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            for (Entry<String, String> entry : map.entrySet()) {
                nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
            httpPost.setEntity(new UrlEncodedFormEntity(nvps));

//			String stringEntity = "param="+paramStr;
//			logger.info("yunman 充值参数{}", stringEntity);
//			StringEntity entity1 = new StringEntity(stringEntity, "utf-8");
//			httpPost.setEntity(entity1);

            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                String resultString = EntityUtils.toString(entity);
                logger.info("yunman 充值同步返回结果{}", resultString);
                result = JSON.parseObject(resultString);
            }

        } catch (Exception ex) {
            logger.warn("订购失败,exMsg:{}; costTime:{}", ex.getMessage(), (System.currentTimeMillis() - begin));
            throw new ServiceException(ResultCode.FAILED);
        } finally {
            logger.info("订购结束; result:{}; costTime:{}", result, (System.currentTimeMillis() - begin));
        }

        if (result.getString("code") != null) {
            if ("0".equals(result.getString("code"))) {
                logger.info("orderId:{},订购成功", order.getId());
                String rechargId = result.getString("sendid");
                if (StringUtils.isEmpty(rechargId)) {
                    logger.warn("yunman充值返回rechargeId为空");
                    throw new ServiceException(ResultCode.FAILED);
                }
                abstractOrderService.setRechargeId(order.getId(), rechargId, "yunman");
            } else {
                String failedReason = result.getString("description");
                // 订购失败
                logger.info("{}充值失败，失败原因{}", order.getId(), failedReason);
//				rechargeService.rechargeSuccess(order.getId(), order.getUsername(), order.getNotifyUrl(), "N", failedReason, order.getExternalId());
                rechargeService.rechargeFailed(order.getId(), order.getUsername(), order.getNotifyUrl(), failedReason, order.getExternalId());
            }
        } else {
            logger.warn("订购返回结果为null");
            throw new ServiceException(ResultCode.FAILED);
        }
    }

    @Override
    public void queryResult(Order order) {
        JSONObject result = null;
        long begin = System.currentTimeMillis();

        HashMap<String, String> map = new HashMap<String, String>();

        JSONObject jParam = new JSONObject();
        jParam.put("partyId", partyId);
        jParam.put("type", "0");
        jParam.put("sendId", order.getRechargeId());
        String sign = "";
        try {
            //MD5(partyId+partyId+type+type+sendid+sendid+key+key)
            sign = MD5Util.computeMD5("partyId" + partyId + "type" + 0 + "sendId" + order.getRechargeId() + "key" + key);
//			sign = EncoderByMd5("partyId"+partyId+"type"+0+"sendId"+order.getRechargeId()+"key"+key);
        } catch (Exception e) {
            logger.warn("签名异常{}", e);
            throw new ServiceException(ResultCode.FAILED);
        }

        jParam.put("sign", sign);
        String param = jParam.toString();

        map.put("param", param);
        try {
            CloseableHttpClient httpClient = HttpClients.custom().build();
            HttpPost httpPost = new HttpPost(QUERY_SERVER_URL);

            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            for (Entry<String, String> entry : map.entrySet()) {
                nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
            httpPost.setEntity(new UrlEncodedFormEntity(nvps));

//			StringEntity entity1 = new StringEntity("param="+param, "utf-8");
//			httpPost.setEntity(entity1);

            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                String resutString = EntityUtils.toString(entity);
                logger.info("yunman 充值查询返回结果{}", resutString);
                result = JSON.parseObject(resutString);
            }
        } catch (Exception ex) {
            logger.warn("查询订单失败, exMsg:{}; costTime:{}", ex.getMessage(), (System.currentTimeMillis() - begin));
            throw new ServiceException(ResultCode.FAILED);
        } finally {
            logger.info("查询订单结束; result:{}; costTime:{}", result, (System.currentTimeMillis() - begin));
        }

        if (result != null) {
            if ("0".equals(result.getString("code"))) {
                logger.info("orderId：{}订购成功", order.getId());
                rechargeService.rechargeSuccess(order.getId(), order.getUsername(), order.getNotifyUrl(), "Y", "订购成功", order.getExternalId());
            } else if ("2".equals(result.getString("code"))) {
                // do nothing
            } else if ("1".equals(result.getString("code"))) {
                logger.info("orderId：{}订购失败", order.getId());

                String failReson = "";
                try {
                    failReson = result.getString("desc");
                } catch (Exception e) {
                    logger.warn("orderId：{}获取失败原因元素不存在", order.getId());
                }
//				rechargeService.rechargeSuccess(order.getId(), order.getUsername(), order.getNotifyUrl(), "N", failReson, order.getExternalId());
                rechargeService.rechargeFailed(order.getId(), order.getUsername(), order.getNotifyUrl(), failReson, order.getExternalId());
            } else {
                //1、2、3什么都不做

//				4.2订单状态码说明
//				1：订单提交成功；
//				2：待充值；
//				3：已提交，等待处理结果；
//				4：充值成功；
//				5：充值失败；
            }
        } else {
            logger.warn("订单查询返回结果为null");
            throw new ServiceException(ResultCode.FAILED);
        }
    }

    @Override
    public void callBack(RechargeResult rechargeResult) {
        try {
            // 根据充值单号查询三天内的订单信息
            Order order = abstractOrderService.findByCreateTimeAfterAndOrderId(
                    new Date(System.currentTimeMillis() - 3 * 24 * 60 * 60 * 1000), rechargeResult.getOrderId());

            if (order == null) {
                logger.warn("未找到充值单号为{}的订单", rechargeResult.getRechargeId());
            } else {
                if (order.getRechargeState().getOper() == RechargeState.PROCESS.getOper()) {
//					rechargeService.rechargeSuccess(order.getId(), order.getUsername(), order.getNotifyUrl(), rechargeResult.getCode(),
//							rechargeResult.getMsg(), order.getExternalId());
                    if (rechargeResult.getCode().equals("N")) {
                        rechargeService.rechargeFailed(order.getId(), order.getUsername(), order.getNotifyUrl(), rechargeResult.getMsg(), order.getExternalId());
                    } else {
                        rechargeService.rechargeSuccess(order.getId(), order.getUsername(), order.getNotifyUrl(), rechargeResult.getCode(),
                                rechargeResult.getMsg(), order.getExternalId());
                    }
                }
            }
        } catch (Exception ex) {
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

    /**
     * 利用MD5进行加密
     *
     * @param str 待加密的字符串
     * @return 加密后的字符串
     * @throws NoSuchAlgorithmException     没有这种产生消息摘要的算法
     * @throws UnsupportedEncodingException
     */
    public String EncoderByMd5(String str) {
        try {
            //确定计算方法
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            BASE64Encoder base64en = new BASE64Encoder();
            //加密后的字符串
            String newstr = base64en.encode(md5.digest(str.getBytes("utf-8")));
            return newstr;
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

}