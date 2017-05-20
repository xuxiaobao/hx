package com.miaosu.controller.recharge;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.miaosu.base.ResultCode;
import com.miaosu.base.ServiceException;
import com.miaosu.service.recharge.RechargeResult;
import com.miaosu.service.recharge.domain.AippRequest;
import com.miaosu.service.recharge.sup.AippRecharge;
import com.miaosu.util.AESUtilApp;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Administrator on 2017/5/20.
 */
@RestController
@RequestMapping(value = "/notify/aipp")
public class AippNotifyController {

    private static final Logger notifyLog = LoggerFactory.getLogger("notify");

    @Value("${aipp.partyid}")
    private String partyId = "898198947782";

    @Value("${aipp.secretkey}")
    private String secretKey = "2a4fb82ae84b1e26eb864362e1155fea";

    @Autowired
    private AippRecharge aippRecharge;

    @RequestMapping(value = "/orderstatus", method = { RequestMethod.GET, RequestMethod.POST })
    public Object orderStatus(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        String jsonString = IOUtils.toString(request.getInputStream());
        notifyLog.info("aipp充值结果通知：{}", jsonString);

        try {
            AippRequest aippRequest = JSON.parseObject(jsonString, AippRequest.class);
            if (!partyId.equals(aippRequest.getPartyId())) {
                notifyLog.info("aipp充值结果通知partyId错误：{}", aippRequest.getPartyId());
                throw new ServiceException(ResultCode.FAILED);
            }
            String jsonData = AESUtilApp.decrypt(aippRequest.getData(), secretKey);


        }catch (Exception ex) {
           //fail
        }


        JSONObject json = JSONObject.parseObject(jsonString);

        String orderId = json.getString("channel_order_id");
        String orderStatus = json.getString("order_status");

        RechargeResult rechargeResult = new RechargeResult();
        rechargeResult.setOrderId(orderId);
        rechargeResult.setCode(orderStatus.equals("5") ? "Y" : "N");

        aippRecharge.callBack(rechargeResult);
        return null;
    }

}
