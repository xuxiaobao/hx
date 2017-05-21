package com.miaosu.controller.recharge;

import com.alibaba.fastjson.JSON;
import com.miaosu.base.ResultCode;
import com.miaosu.base.ServiceException;
import com.miaosu.service.recharge.RechargeResult;
import com.miaosu.service.recharge.domain.AippNotifyResult;
import com.miaosu.service.recharge.domain.AippRequest;
import com.miaosu.service.recharge.sup.AippRecharge;
import com.miaosu.util.AESUtilApp;
import com.miaosu.util.MD5UtilApp;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        Map<String, String> returnMap = new HashMap<>();
        returnMap.put("status", "1");
        returnMap.put("resultCode", "00000");
        try {
            AippRequest aippRequest = JSON.parseObject(jsonString, AippRequest.class);
            if (!partyId.equals(aippRequest.getPartyId())) {
                notifyLog.info("aipp充值结果通知partyId错误：{}", aippRequest.getPartyId());
                throw new ServiceException(ResultCode.FAILED);
            }
            String sign = MD5UtilApp.getSignAndMD5(aippRequest.getPartyId(), aippRequest.getData(), aippRequest.getTime());
            if (sign==null || (!sign.equals(aippRequest.getSign()))) {
                notifyLog.info("aipp充值结果通知签名错误");
                throw new ServiceException(ResultCode.FAILED);
            }
            String jsonData = AESUtilApp.decrypt(aippRequest.getData(), secretKey);
            List<AippNotifyResult> list = JSON.parseArray(jsonData, AippNotifyResult.class);
            if (list != null) {

                for(AippNotifyResult data : list) {

                    String orderId = data.getOrderId();
                    String orderStatus = data.getStatus();
                    String resultDesc = data.getResultDesc();
                    RechargeResult rechargeResult = new RechargeResult();
                    rechargeResult.setOrderId(orderId);
                    rechargeResult.setCode(orderStatus.equals("1") ? "Y" : "N");
                    rechargeResult.setMsg(resultDesc);

                    aippRecharge.callBack(rechargeResult);
                }
            }

        }catch (Exception ex) {
           returnMap.put("status", "0");
           returnMap.put("resultCode", "51000");
           notifyLog.info("aipp充值结果通知异常");
        }

        return returnMap;
    }

}
