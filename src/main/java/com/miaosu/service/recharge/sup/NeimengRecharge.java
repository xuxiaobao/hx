package com.miaosu.service.recharge.sup;

import com.miaosu.base.ResultCode;
import com.miaosu.base.ServiceException;
import com.miaosu.model.Order;
import com.miaosu.service.orders.AbstractOrderService;
import com.miaosu.service.recharge.AbstractRecharge;
import com.miaosu.service.recharge.RechargeResult;
import com.miaosu.service.recharge.RechargeService;
import com.miaosu.util.Constants;
import com.miaosu.util.DateUtil;
import com.miaosu.util.XmlUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created by xxb on 2017/5/15.
 */
@Service
public class NeimengRecharge extends AbstractRecharge {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private AbstractOrderService abstractOrderService;

    @Autowired
    private RechargeService rechargeService;


    @Override
    public void recharge(Order order) {
        /*
        鉴权
         */

        if (true) {
            /*
            参数
             */
            StringBuilder builder = new StringBuilder();
            String dateTime = DateUtil.formatDate(new Date());
            String sign = DigestUtils.sha256Hex(Constants.AppKey.concat(dateTime).concat(Constants.AppSecret));
            builder.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
            builder.append("<Request>");
            builder.append("<Datetime>").append(dateTime).append("</Datetime>");
            builder.append("<Authorization>");
            builder.append("<AppKey>").append(Constants.AppKey).append("</AppKey>");
            builder.append("<Sign>").append(sign).append("</Sign>");
            builder.append("</Authorization>");
            builder.append("</Request>");
            String param = builder.toString();

            //创建post请求
            HttpPost post = new HttpPost(Constants.SERVER_URL.concat("auth.html"));
            post.setHeader("Content-Type", "application/xml");
            post.setHeader("4GGOGO-Auth-Token", "");
            post.setHeader("HTTP-X-4GGOGO-Signature", "");
            post.setEntity(new StringEntity(param, "UTF-8"));
            String resutString = null;
            try {
                CloseableHttpClient httpClient = HttpClients.custom().build();
                CloseableHttpResponse response = httpClient.execute(post);
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    resutString = EntityUtils.toString(entity);
                    System.out.println("鉴权返回结果："+resutString);

                    resutString = XmlUtil.replaceBlank(resutString);
                    Document document = DocumentHelper.parseText(resutString);
                    Element root = document.getRootElement().element("Authorization");
                    String token = root.element("Token").getText();
                    String expiredTime = root.element("ExpiredTime").getText();
                    Constants.TokenMap.put("Token", token);
                    Constants.TokenMap.put("ExpiredTime", expiredTime);
                }
            } catch (Exception ex) {

            } finally {
                System.out.println("鉴权结束");
            }
        }
        /*
        充值
         */
        String systemNum = null;
        if (true) {
            System.out.println("充值开始");
            StringBuilder builder = new StringBuilder();
            Date dt = new Date();
            String dateTime = DateUtil.formatDate(dt);
            builder.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
            builder.append("<Request>");
            builder.append("<Datetime>").append(dateTime).append("</Datetime>");
            builder.append("<ChargeData>");
            builder.append("<Mobile>").append(order.getPhone()).append("</Mobile>");
            builder.append("<ProductId>").append(order.getProductId()).append("</ProductId>");
            builder.append("<SerialNum>").append(DateUtil.formatNormalDate(dt)).append("</SerialNum>");
            builder.append("</ChargeData>");
            builder.append("</Request>");
            String param = builder.toString();
            String token = Constants.TokenMap.get("Token");
            String sign = DigestUtils.sha256Hex(param.concat(Constants.AppSecret));

            try {
                HttpPost post = new HttpPost(Constants.SERVER_URL+"boss/charge.html");
                post.setHeader("Content-Type", "application/xml");
                post.setHeader("4GGOGO-Auth-Token", token);
                post.setHeader("HTTP-X-4GGOGO-Signature", sign);
                post.setEntity(new StringEntity(param, "UTF-8"));
                CloseableHttpClient httpClient = HttpClients.custom().build();
                CloseableHttpResponse response = httpClient.execute(post);
                HttpEntity entity = response.getEntity();
                String resutString = null;
                if (entity != null) {
                    resutString = EntityUtils.toString(entity);
                    System.out.println("充值返回结果是："+resutString);
                    resutString = XmlUtil.replaceBlank(resutString);
                    Document document = DocumentHelper.parseText(resutString);
                    Element chargeData = document.getRootElement().element("ChargeData");
                    systemNum = chargeData.element("SystemNum").getText();
                    if (StringUtils.isEmpty(systemNum)) {
                        new RuntimeException("充值单号为空");
                    }
                    order.setRechargeId(systemNum);
                    abstractOrderService.setRechargeId(order.getId(), systemNum, "neimeng");
                }
            } catch (Exception ex) {

            } finally {
                System.out.println("充值结束");
            }
        }
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            throw new ServiceException(ResultCode.FAILED);
        }
        /*
        查询结果
         */
        if (true) {
            System.out.println("查询充值结果");
            String status = null;
            String description = null;
            try {
                if (StringUtils.isNotEmpty(systemNum)) {
                    String token = Constants.TokenMap.get("Token");
                    String sign = DigestUtils.sha256Hex(Constants.AppSecret);
                    HttpGet get = new HttpGet(Constants.SERVER_URL+"chargeRecords/"+systemNum+".html");
                    get.setHeader("Content-Type", "application/xml;charset=utf-8");
                    get.setHeader("4GGOGO-Auth-Token", token);
                    get.setHeader("HTTP-X-4GGOGO-Signature", sign);
                    CloseableHttpClient httpClient = HttpClients.custom().build();
                    CloseableHttpResponse response = httpClient.execute(get);
                    HttpEntity entity = response.getEntity();
                    if (entity != null) {
                        String resultString = EntityUtils.toString(entity,"UTF-8");
                        System.out.println("查询充值返回结果是："+resultString);
                        resultString = XmlUtil.replaceBlank(resultString);
                        Document document = DocumentHelper.parseText(resultString);
                        Element record = document.getRootElement().element("Records").element("Record");
                        status = record.element("Status").getText();
                        description = record.element("Description").getText();
                    }
                } else {
                    throw new RuntimeException("充值单号为空");
                }
            } catch (Exception ex) {

            } finally {
                System.out.println("查询充值结果结束");
            }
            if (status != null) {
                if ("3".equals(status)) {
                    rechargeService.rechargeSuccess(order.getId(), order.getUsername(), order.getNotifyUrl(), "Y", "订购成功", order.getExternalId());
                } else if ("4".equals(status)) {
                    rechargeService.rechargeFailed(order.getId(), order.getUsername(), order.getNotifyUrl(),description, order.getExternalId());
                } else {
                    //正在充值
                }
            } else {
                throw new RuntimeException("订单查询充值结果为null");
            }
        }

    }

    @Override
    public void queryResult(Order order) {

    }

    @Override
    public void callBack(RechargeResult rechargeResult) {

    }

}
