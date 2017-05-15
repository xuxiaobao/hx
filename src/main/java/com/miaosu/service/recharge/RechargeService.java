package com.miaosu.service.recharge;

import com.miaosu.controller.openapi.OpenSign;
import com.miaosu.model.Member;
import com.miaosu.model.Order;
import com.miaosu.model.enums.RechargeState;
import com.miaosu.service.acct.AcctService;
import com.miaosu.service.members.MemberService;
import com.miaosu.service.orders.AbstractOrderService;
import com.miaosu.service.recharge.task.Callback;
import com.miaosu.util.DESUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 充值服务
 */
@Service
public class RechargeService {
	
    private static final Logger logger = LoggerFactory.getLogger(RechargeService.class);
    
    private static final Logger CallBackLog = LoggerFactory.getLogger("callback");

    @Autowired
    private AbstractOrderService abstractOrderService;

    @Autowired
    private AcctService acctService;

    @Resource(name = "supRechargeMap")
    private Map<String , AbstractRecharge> supRechargeMap ;

    @Autowired
    private MemberService memberService;

    private ExecutorService pool = Executors. newFixedThreadPool(30);

    @Autowired
    @Qualifier("defaultRestTemplate")
    private RestTemplate restTemplate;

    public void recharge(Order order){
        AbstractRecharge abstractRecharge = supRechargeMap.get(order.getSupId());
        if (abstractRecharge == null){
            logger.warn("供货商不存在，赶紧检查下，supId:{}",order.getSupId());
            return;
        }
        abstractRecharge.recharge(order);
    }

    public void resultQuery(Order order){
        AbstractRecharge abstractRecharge = supRechargeMap.get(order.getSupId());
        if (abstractRecharge == null){
            logger.warn("供货商不存在，赶紧检查下，supId:{}",order.getSupId());
            return;
        }
        abstractRecharge.queryResult(order);
    }


    public void rechargeSuccess(String orderId, String userName, String notifyUrl, String status, String failedReason, String externalId){
        try {
            logger.info("成功订单处理,orderId:{}",orderId);
            abstractOrderService.rechargeSuccess(orderId);

            // 回调地址不为空时，添加一条回调信息到队列
            if (StringUtils.isNotEmpty(notifyUrl)) {
                this.callBack(orderId, externalId, userName, RechargeState.SUCCESS, notifyUrl, failedReason);
            }
        } catch (Exception e) {
            logger.warn("处理{}充值结果发生异常；status:{}, failedReason:{}", orderId, status, failedReason, e);
        }
    }

    public void rechargeFailed(String orderId, String userName, String notifyUrl, String failedReason, String externalId){
        logger.info("失败订单处理,orderId:{}",orderId);

        Order order = abstractOrderService.get(orderId);
        logger.info("orderId：{}支持的供货商{},当前的供货商{}",order.getId(),order.getSupList(),order.getSupId());
        try {

            if (StringUtils.isNotEmpty(order.getSupList()) && StringUtils.isNotEmpty(order.getSupId())){
                int inx = StringUtils.lastIndexOf(order.getSupList(),order.getSupId());

                if (inx+1<order.getSupList().split(",").length){

                    String nextSup = order.getSupList().split(",")[inx+1];

                    order.setSupId(nextSup);

                    //        更新供货商信息
                    abstractOrderService.updateSupInfo(orderId,nextSup,order.getSupList());

                    abstractOrderService.setToInit(orderId);

                    abstractOrderService.setToRecharging(orderId);

                    logger.info("orderId：{},支持的供货商{},下一个供货商{}",order.getId(),order.getSupList(),nextSup);

                    return;
                }
            }
        } catch (Exception e) {
            logger.warn("变更供货商出错",e);
        }

        String subReason = failedReason != null && failedReason.length() > 1000 ? failedReason.substring(0, 1000) + "......" : failedReason;
        // 订购失败，更新订单失败原因、生成退款流水
        String refundBillId = abstractOrderService.rechargeFailed(orderId, subReason);

        // 退款
        logger.info("发起对{}订单进行退款，退款单号{}", orderId, refundBillId);
        acctService.refund(userName, refundBillId, orderId);

        // 回调地址不为空时，添加一条回调信息到队列
        if (StringUtils.isNotEmpty(notifyUrl)) {
            this.callBack(orderId, externalId, userName, RechargeState.FAILED, notifyUrl, failedReason);
        }
    }

    public void callBack(final String orderId, final String externalId, final String userName, final RechargeState rechargeState,
                         final String notifyUrl,final String failedReason){
        logger.info("开始回调商户,orderId:{},externalId:{},userName:{},rechargeState:{},notifyUrl:{},failedReason:{}",
                orderId, externalId, userName, rechargeState, notifyUrl, failedReason);
        pool.execute(new Runnable() {
            @Override
            public void run() {
                doCallback(new Callback(orderId, externalId, userName, rechargeState, notifyUrl, failedReason));
            }
        });
    }


    /**
     * 执行回调
     */
    public void doCallback(Callback callback) {
        long begin = System.currentTimeMillis();

        Map<String, Object> paramMap = new HashMap<>();
        String notifyUrl = callback.getNotifyUrl();
        String result = null;
        try {
            // 回调地址不为空才执行回调；
            if (!org.springframework.util.StringUtils.hasText(notifyUrl)) {
                return;
            }

            // 获取用户密钥
            String secret = getSecret(callback.getUserName());
            if (secret == null) {
                return;
            }

            paramMap.put("orderId", callback.getOrderId());
            paramMap.put("transId", callback.getTransId());
            paramMap.put("status", callback.getRechargeState().ordinal());
            paramMap.put("failedReason", callback.getFailedReason());
            String sign = OpenSign.getSign(paramMap, secret);
            paramMap.put("sign", sign);

            result = restTemplate.getForObject(
                    notifyUrl + "?orderId={orderId}&transId={transId}&status={status}&failedReason={failedReason}&sign={sign}",
                    String.class, paramMap);
        } catch (Exception ex) {
        	CallBackLog.warn("回调失败; notifyUrl:{}, paramMap:{}, exMsg:{}", notifyUrl, paramMap, ex.getMessage());
        } finally {
        	CallBackLog.info("回调结束; notifyUrl:{}, paramMap:{}; result:{}; costTime:{}", notifyUrl, paramMap, result,
                    (System.currentTimeMillis() - begin));
        }
    }


    /**
     * 获取用户密钥
     * @param userName 用户名
     * @return 密钥
     */
    private String getSecret(String userName) {
        try {
            Member member = memberService.get(userName);
            return DESUtil.decryptToString(member.getToken(), userName);
        } catch (Exception ex) {
            logger.warn("获取用户密钥失败，userName:{}, exMsg:{}", userName, ex.getMessage());
            return null;
        }
    }
}
