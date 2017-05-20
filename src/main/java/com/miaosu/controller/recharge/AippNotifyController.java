package com.miaosu.controller.recharge;

import com.alibaba.fastjson.JSONObject;
import com.miaosu.service.recharge.RechargeResult;
import com.miaosu.service.recharge.sup.AippRecharge;
import com.miaosu.service.recharge.sup.ZhuowangRecharge;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Administrator on 2017/5/20.
 */
@Controller
@RequestMapping(value = "/notify/aipp")
public class AippNotifyController {

    private static final Logger notifyLog = LoggerFactory.getLogger("notify");


    @Autowired
    private AippRecharge aippRecharge;

    @RequestMapping(value = "/orderstatus", method = { RequestMethod.GET, RequestMethod.POST })
    public void orderStatus(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        String jsonString = IOUtils.toString(request.getInputStream());
        notifyLog.info("zhuowang充值结果通知：{}", jsonString);

        JSONObject json = JSONObject.parseObject(jsonString);

        String orderId = json.getString("channel_order_id");
        String orderStatus = json.getString("order_status");

        RechargeResult rechargeResult = new RechargeResult();
        rechargeResult.setOrderId(orderId);
        rechargeResult.setCode(orderStatus.equals("5") ? "Y" : "N");

        aippRecharge.callBack(rechargeResult);
    }

}
