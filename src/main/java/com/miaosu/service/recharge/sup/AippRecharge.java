package com.miaosu.service.recharge.sup;

import com.alibaba.fastjson.JSON;
import com.miaosu.base.ResultCode;
import com.miaosu.base.ServiceException;
import com.miaosu.mapper.SupMapper;
import com.miaosu.model.Order;
import com.miaosu.model.Sup;
import com.miaosu.model.enums.RechargeState;
import com.miaosu.service.orders.AbstractOrderService;
import com.miaosu.service.productsup.ProductSupService;
import com.miaosu.service.recharge.AbstractRecharge;
import com.miaosu.service.recharge.RechargeResult;
import com.miaosu.service.recharge.RechargeService;
import com.miaosu.service.recharge.domain.*;
import com.miaosu.util.AESUtilApp;
import com.miaosu.util.DateUtilsApp;
import com.miaosu.util.HttpUtilApp;
import com.miaosu.util.MD5UtilApp;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created by Administrator on 2017/5/19.
 */
@Service
public class AippRecharge extends AbstractRecharge {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${aipp.partyid}")
    private String partyId = "898198947782";
    @Value("${aipp.secretkey}")
    private String secretKey = "2a4fb82ae84b1e26eb864362e1155fea";
    @Value("${aipp.server}")
    private String SERVER_URL = "http://117.177.222.131:11080";
    @Value("${aipp.notify}")
    private String SERVER_NOTIFY;

    @Autowired
    private AbstractOrderService abstractOrderService;

    @Autowired
    private RechargeService rechargeService;

    @Autowired
    private SupMapper supMapper;


    @Override
    public void recharge(Order order) {
        logger.info("订购开始");
        long begin = System.currentTimeMillis();

        /*密钥，资源平台方提供*/
        String secretKey = this.secretKey;
        AippPurchaseRequest aippPurchaseRequest = new AippPurchaseRequest();
        /*协议ID*/
        Sup sup = supMapper.selectInfo(order.getSupId());
        aippPurchaseRequest.setProtocolId(sup.getSupName());

		/*订单号*/
		aippPurchaseRequest.setOrderId(order.getId());

		/*订单请求时间*/
		aippPurchaseRequest.setOrderTime(DateUtilsApp.getCurrDateTime("yyyy-MM-dd HH:mm:ss"));

		/*产品ID*/
		aippPurchaseRequest.setProdId(order.getProductId());

		/*订购号码*/
		aippPurchaseRequest.setPhoneNumber(order.getPhone());

		/*回调地址*/
		aippPurchaseRequest.setNotifyUrl(SERVER_NOTIFY);

        AippRequest request = new AippRequest();
        try {
            String dataJson = AESUtilApp.encrypt(JSON.toJSONString(aippPurchaseRequest), secretKey);
            request.setPartyId(this.partyId);
            request.setData(dataJson);
            request.setTime(DateUtilsApp.getCurrDateTime("yyyyMMddHHmmssSSS"));
            String sign = MD5UtilApp.getSignAndMD5(request.getPartyId(), request.getData(), request.getTime());
            request.setSign(sign);
        } catch (Exception ex) {
            logger.warn("签名异常:{}", ex);
            throw new ServiceException(ResultCode.FAILED);
        }

        /*
        订购
         */
        String resutString = null;
        String failedReason = "发送充值请求失败";
        try {
            String param = JSON.toJSONString(request);
            resutString = HttpUtilApp.doPost(this.SERVER_URL+"/order/purchase", param);
            System.out.println(resutString);
            if (StringUtils.isEmpty(resutString)) {
                throw new ServiceException(ResultCode.FAILED);
            }
            AippPurchaseResult purchase = JSON.parseObject(resutString, AippPurchaseResult.class);
            if ("1".equals(purchase.getStatus())) {
                abstractOrderService.setRechargeId(order.getId(), purchase.getData().getChannelOrderId(), "aipp");
            } else {
                failedReason = purchase.getResultDesc();
                throw new ServiceException(ResultCode.FAILED);
            }
        } catch (Exception ex) {
            logger.warn("订购失败,exMsg:{}; costTime:{}", ex.getMessage(), (System.currentTimeMillis() - begin));
            rechargeService.rechargeFailed(order.getId(), order.getUsername(), order.getNotifyUrl(),failedReason, order.getExternalId());
        } finally {
            logger.info("订购结束; result:{}; costTime:{}", resutString, (System.currentTimeMillis() - begin));
        }
    }

    @Override
    public void queryResult(Order order) {
        /*long begin = System.currentTimeMillis();
        logger.info("查询充值开始");

        *//*
        设置查询参数
         *//*
        AippQueryRequest aippQueryRequest = new AippQueryRequest();
        //设置订购方订单号
        aippQueryRequest.setOrderId(order.getId());
        //设置资源平台订单号
        aippQueryRequest.setChannelOrderId(order.getRechargeId());
        AippRequest request = new AippRequest();
        try {
            //请求体json数据
            String dataJson = AESUtilApp.encrypt(JSON.toJSONString(aippQueryRequest), secretKey);
            *//*身份ID*//*
            request.setPartyId(this.partyId);
            *//*data请求体*//*
            request.setData(dataJson);
			*//*请求时间*//*
            request.setTime(DateUtilsApp.getCurrDateTime("yyyyMMddHHmmssSSS"));
            *//*签名*//*
            //MD5加密
            String md5Str = MD5UtilApp.getSignAndMD5(request.getPartyId(),request.getData(),request.getTime());
            request.setSign(md5Str);
        } catch (Exception ex) {
            logger.warn("签名错误");
            throw new ServiceException(ResultCode.FAILED);
        }

        String param = JSON.toJSONString(request);
        String resultString = null;

        try {
            resultString = HttpUtilApp.doPost(this.SERVER_URL+"/order/query",param);
            if (StringUtils.isEmpty(resultString)) {
                throw new ServiceException(ResultCode.FAILED);
            }
            AippQueryResult aippQueryResult = JSON.parseObject(resultString, AippQueryResult.class);
            *//*请求结果*//*
            String status = aippQueryResult.getStatus();
            if ("1".equals(status)) {
                rechargeService.rechargeSuccess(order.getId(), order.getUsername(), order.getNotifyUrl(), "Y", "订购成功", order.getExternalId());
            } else {
                String description = aippQueryResult.getResultDesc();
                rechargeService.rechargeFailed(order.getId(), order.getUsername(), order.getNotifyUrl(),description, order.getExternalId());
            }
        } catch (Exception ex) {
            logger.warn("查询充值结果失败");
        } finally {
            logger.info("查询充值结果结束; result:{}; costTime:{}", resultString, (System.currentTimeMillis() - begin));
        }*/

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
                throw new ServiceException(ResultCode.FAILED);
            }
            else
            {
                int a = order.getRechargeState().getOper();
                int b = RechargeState.PROCESS.getOper();
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
            throw new ServiceException(ResultCode.FAILED);
        }
    }

}
