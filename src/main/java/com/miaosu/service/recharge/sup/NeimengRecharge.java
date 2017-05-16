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
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

/**
 * Created by xxb on 2017/5/15.
 */
@Service
public class NeimengRecharge extends AbstractRecharge {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${neimeng.appkey}")
    private String appKey = "6d685cb0d75649e090ccc08f71413226";

    @Value("${neimeng.appsecret}")
    private String appSecret = "ffd75688431d47a6b03192685fa64b3f";

    @Value("${neimeng.server}")
    private String SERVER_URL = "http://www.nm.10086.cn/flowplat/";

    @Autowired
    private AbstractOrderService abstractOrderService;

    @Autowired
    private RechargeService rechargeService;


    @Override
    public void recharge(Order order) {
        logger.info("开始进行充值：{}", order.getId());
        long begin = System.currentTimeMillis();
        if (!checkToken()) {
            authToken();
        }
        StringBuilder builder = new StringBuilder();
        Date dt = new Date();
        String dateTime = DateUtil.formatDate(dt);
        String serialNum = UUID.randomUUID().toString().toLowerCase().replace("-", "");
        builder.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
        builder.append("<Request>");
        builder.append("<Datetime>").append(dateTime).append("</Datetime>");
        builder.append("<ChargeData>");
        builder.append("<Mobile>").append(order.getPhone()).append("</Mobile>");
        builder.append("<ProductId>").append(order.getProductId()).append("</ProductId>");
        builder.append("<SerialNum>").append(serialNum).append("</SerialNum>");
        builder.append("</ChargeData>");
        builder.append("</Request>");
        String param = builder.toString();
        String token = Constants.TokenMap.get("Token");
        String sign = DigestUtils.sha256Hex(param.concat(appKey));
        Map<String, String> result = HttpClientUtil.doPost(SERVER_URL.concat("boss/charge.html"), param, token, sign);
        logger.info("订购结束; result:{}; costTime:{}", (result==null)?null:result.get("response"), (System.currentTimeMillis() - begin));
        if (result != null) {
            if (StringUtils.isNotBlank(result.get("response"))) {
                logger.info("neimeng 充值同步返回结果{}", result.get("response"));
                if ("200".equals(result.get("status"))) {
                    Document document = null;
                    try {
                        document = DocumentHelper.parseText(result.get("response"));
                    } catch (DocumentException e) {
                        logger.warn("neimeng充值返回结果异常");
                    }
                    String systemNum = document.getRootElement().element("SystemNum").getText();
                    if (StringUtils.isEmpty(systemNum)) {
                        logger.warn("neimeng充值返回SystemNum为空");
                        throw new ServiceException(ResultCode.FAILED);
                    }
                    abstractOrderService.setRechargeId(order.getId(), systemNum, "neimeng");
                } else {
                    String failedReason = result.get("status");
                    // 订购失败
                    logger.info("{}充值失败，失败原因{}", order.getId(), failedReason);
                    rechargeService.rechargeSuccess(order.getId(), order.getUsername(), order.getNotifyUrl(), "N", failedReason, order.getExternalId());
                }
            } else {
                logger.warn("订购返回结果为null");
                throw new ServiceException(ResultCode.FAILED);
            }
        } else {
            logger.warn("订购返回结果为null");
            throw new ServiceException(ResultCode.FAILED);
        }
        query(order);
    }

    @Override
    public void queryResult(Order order) {

    }


    private void query(Order order) {
        long begin = System.currentTimeMillis();
        if (!checkToken()) {
            authToken();
        }
        String token = Constants.TokenMap.get("Token");
        String sign = DigestUtils.sha256Hex(Constants.AppSecret);
        String url = SERVER_URL.concat(order.getRechargeId()).concat(".html");
        Map<String, String> response = HttpClientUtil.doGet(url, token, sign);
        logger.info("查询订单结束; result:{}; costTime:{}", (response==null?null:response.get("response")), (System.currentTimeMillis() - begin));
        if (response != null) {
            if (StringUtils.isNotEmpty(response.get("response"))) {
                logger.info("neimeng 充值查询返回结果{}", response.get("response"));
                if ("200".equals(response.get("status"))) {
                    Document document = null;
                    try {
                        document = DocumentHelper.parseText(response.get("response"));
                        String status = document.getRootElement().element("Status").getText();
                        if ("3".equals(status)) {
                            logger.info("orderId：{}订购成功", order.getId());
                            rechargeService.rechargeSuccess(order.getId(), order.getUsername(), order.getNotifyUrl(), "Y", "订购成功", order.getExternalId());
                        } else if ("2".equals(status)) {
                            //do something
                        } else if ("1".equals(status)) {
                            //do something
                        } else {
                            logger.info("orderId：{}订购失败", order.getId());

                            String failReson = "";
                            try
                            {
                                failReson = document.getRootElement().element("Description").getText();
                            }
                            catch (Exception e)
                            {
                                logger.warn("orderId：{}获取失败原因元素不存在", order.getId());
                            }
                            rechargeService.rechargeSuccess(order.getId(), order.getUsername(), order.getNotifyUrl(), "N", failReson, order.getExternalId());
                        }
                    } catch (DocumentException e) {
                        logger.warn("订单查询返回结果为异常");
                        throw new ServiceException(ResultCode.FAILED);
                    }

                } else {
                    logger.warn("查询订单失败, exMsg:{}; costTime:{}", response.get("response"), (System.currentTimeMillis() - begin));
                    throw new ServiceException(ResultCode.FAILED);
                }
            } else {
                logger.warn("订单查询返回结果为null");
                throw new ServiceException(ResultCode.FAILED);
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

    private boolean authToken() {
        StringBuilder builder = new StringBuilder();
        String dateTime = DateUtil.formatDate(new Date());
        String sign = DigestUtils.sha256Hex(appKey.concat(dateTime).concat(appSecret));
        builder.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
        builder.append("<Request>");
        builder.append("<Datetime>").append(dateTime).append("</Datetime>");
        builder.append("<Authorization>");
        builder.append("<AppKey>").append(appKey).append("</AppKey>");
        builder.append("<Sign>").append(sign).append("</Sign>");
        builder.append("</Authorization>");
        builder.append("</Request>");
        Map<String, String> result = HttpClientUtil.doPost(SERVER_URL.concat("/auth.html"), builder.toString());
        if (StringUtils.isNotBlank(result.get("response"))) {
            try {
                Document document = DocumentHelper.parseText(result.get("response"));
                Element root = document.getRootElement().element("Authorization");
                String token = root.element("Token").getText();
                String expiredTime = root.element("ExpiredTime").getText();
                Constants.TokenMap.put("Token", token);
                Constants.TokenMap.put("ExpiredTime", expiredTime);
                return true;
            } catch (DocumentException e) {
                logger.warn("签名异常{}", e);
                throw new ServiceException(ResultCode.FAILED);
            }
        }
        return false;
    }

    private boolean checkToken() {
        String token = Constants.TokenMap.get("Token");
        String expiredTime = Constants.TokenMap.get("ExpiredTime");
        if (StringUtils.isNotBlank(token)) {
            Date dt = DateUtil.parseDate(expiredTime);
            long now = System.currentTimeMillis()-1000;
            if (now >= dt.getTime()) {
                if (authToken()) {
                    return true;
                }
            }else {
                return true;
            }
        }
        return false;
    }
}
