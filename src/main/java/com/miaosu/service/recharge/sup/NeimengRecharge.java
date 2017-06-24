package com.miaosu.service.recharge.sup;

import com.miaosu.base.ResultCode;
import com.miaosu.base.ServiceException;
import com.miaosu.model.Order;
import com.miaosu.model.enums.RechargeState;
import com.miaosu.service.orders.AbstractOrderService;
import com.miaosu.service.recharge.AbstractRecharge;
import com.miaosu.service.recharge.RechargeResult;
import com.miaosu.service.recharge.RechargeService;
import com.miaosu.util.Constants;
import com.miaosu.util.DateUtil;
import com.miaosu.util.HttpClientUtil;
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
import java.util.HashMap;
import java.util.Map;

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
        logger.info("开始进行充值：{}", order.getId());
        long begin = System.currentTimeMillis();
        /*
        鉴权
         */

        boolean ok = checkToken();
        if (ok) {
            authToken();
        }
        /*
        充值
         */
        String systemNum = order.getRechargeId();
        if (StringUtils.isEmpty(systemNum)) {
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
            String resutString = null;

            Map<String, String> headers = new HashMap<String, String>();
            headers.put("Content-Type", "application/xml");
            headers.put("4GGOGO-Auth-Token", token);
            headers.put("HTTP-X-4GGOGO-Signature", sign);
            try {
                resutString = HttpClientUtil.doPost(Constants.SERVER_URL+"boss/charge.html", param, headers);
                if (StringUtils.isNotEmpty(resutString)) {
                    resutString = XmlUtil.replaceBlank(resutString);
                    Document document = DocumentHelper.parseText(resutString);
                    Element chargeData = document.getRootElement().element("ChargeData");
                    systemNum = chargeData.element("SystemNum").getText();
                    if (StringUtils.isEmpty(systemNum)) {
                        logger.warn("订购返回流水号为空");
                        throw new ServiceException(ResultCode.FAILED);
                    }
                    abstractOrderService.setRechargeId(order.getId(), systemNum, "neimeng");
                } else {
                    throw new ServiceException(ResultCode.FAILED);
                }
            } catch (Exception ex) {
                /*
                充值失败
                 */
                String failedReason = "充值请求返回结果异常";
                logger.warn("订购失败,exMsg:{}; costTime:{}", ex.getMessage(), (System.currentTimeMillis() - begin));
                rechargeService.rechargeFailed(order.getId(), order.getUsername(), order.getNotifyUrl(),failedReason, order.getExternalId());
            } finally {
                logger.info("订购结束; result:{}; costTime:{}", resutString, (System.currentTimeMillis() - begin));
            }
        }

    }

    @Override
    public void queryResult(Order order) {
        long begin = System.currentTimeMillis();
        logger.info("查询订购结果开始");

        /*
        鉴权
         */
        boolean ok = checkToken();

        if (ok) {
           authToken();
        }

        String status = null;
        String description = null;
        String systemNum = order.getRechargeId();
        String token = Constants.TokenMap.get("Token");
        String sign = DigestUtils.sha256Hex(Constants.AppSecret);
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/xml;charset=utf-8");
        headers.put("4GGOGO-Auth-Token", token);
        headers.put("HTTP-X-4GGOGO-Signature", sign);
        try {
            String resultString = HttpClientUtil.doGet(Constants.SERVER_URL+"chargeRecords/"+systemNum+".html", headers);
            if (StringUtils.isNotEmpty(resultString)) {
                resultString = XmlUtil.replaceBlank(resultString);
                Document document = DocumentHelper.parseText(resultString);
                Element record = document.getRootElement().element("Records").element("Record");
                status = record.element("Status").getText();
                description = record.element("Description").getText();
            } else {
                throw new ServiceException(ResultCode.FAILED);
            }
        } catch (Exception ex) {
            logger.warn("查询充值结果失败");
        } finally {
            logger.info("查询充值结果结束; result:{}; costTime:{}", description, (System.currentTimeMillis() - begin));
        }
        if (status != null) {
            if ("3".equals(status)) {
                rechargeService.rechargeSuccess(order.getId(), order.getUsername(), order.getNotifyUrl(), "Y", "订购成功", order.getExternalId());
            } else if ("4".equals(status)) {
                rechargeService.rechargeFailed(order.getId(), order.getUsername(), order.getNotifyUrl(),description, order.getExternalId());
            }
        } else {
            logger.warn("订单查询返回结果为null");
            throw new ServiceException(ResultCode.FAILED);
        }
    }

    @Override
    public void callBack(RechargeResult rechargeResult) {
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

    public boolean checkToken() {
        String token = Constants.TokenMap.get("Token");
        String expiredTime = Constants.TokenMap.get("ExpiredTime");
        if (StringUtils.isNotEmpty(token)) {
            Date dt = DateUtil.parseDate(expiredTime);
            long now = System.currentTimeMillis()-1000;
            if (dt.getTime() - now > 60000) {
                return false;
            }
        }
        return true;
    }

    public void authToken() {
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
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/xml");
        String resutString = null;
        try {
            resutString = HttpClientUtil.doPost(Constants.SERVER_URL.concat("auth.html"), param, headers);
            if (StringUtils.isNotEmpty(resutString)) {
                resutString = XmlUtil.replaceBlank(resutString);
                Document document = DocumentHelper.parseText(resutString);
                Element root = document.getRootElement().element("Authorization");
                String token = root.element("Token").getText();
                String expiredTime = root.element("ExpiredTime").getText();
                Constants.TokenMap.put("Token", token);
                Constants.TokenMap.put("ExpiredTime", expiredTime);
            } else {
                throw new ServiceException(ResultCode.FAILED);
            }
        } catch (Exception ex) {
            logger.warn("签名异常:{}", ex);
            throw new ServiceException(ResultCode.FAILED);
        }
    }

}
