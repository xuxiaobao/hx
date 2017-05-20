package com.miaosu.service.recharge.sup;

import com.miaosu.model.Order;
import com.miaosu.service.orders.AbstractOrderService;
import com.miaosu.service.recharge.AbstractRecharge;
import com.miaosu.service.recharge.RechargeResult;
import com.miaosu.service.recharge.RechargeService;
import com.miaosu.service.recharge.domain.AippPurchaseRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

/**
 * Created by Administrator on 2017/5/19.
 */
public class AippRecharge extends AbstractRecharge {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${aipp.partyid}")
    private String partyId;
    @Value("${aipp.secretkey}")
    private String secretKey;
    @Value("${aipp.server}")
    private String SERVER_URL;

    @Autowired
    private AbstractOrderService abstractOrderService;

    @Autowired
    private RechargeService rechargeService;

    @Override
    public void recharge(Order order) {
        /*密钥，资源平台方提供*/
        String secretKey = this.secretKey;
        AippPurchaseRequest aippPurchaseRequest = new AippPurchaseRequest();
        aippPurchaseRequest.setProtocolId(order.getProductId());

    }

    @Override
    public void queryResult(Order order) {

    }

    @Override
    public void callBack(RechargeResult rechargeResult) {

    }
}
