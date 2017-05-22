import com.alibaba.fastjson.JSON;
import com.miaosu.base.ResultCode;
import com.miaosu.base.ServiceException;
import com.miaosu.model.Order;
import com.miaosu.service.recharge.domain.*;
import com.miaosu.service.recharge.sup.AippRecharge;
import com.miaosu.util.AESUtilApp;
import com.miaosu.util.DateUtilsApp;
import com.miaosu.util.HttpUtilApp;
import com.miaosu.util.MD5UtilApp;
import org.apache.commons.lang3.StringUtils;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by Administrator on 2017/5/20.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:spring/*.xml")
public class Test {

    @Value("${aipp.partyid}")
    private String partyId = "898198947782";
    @Value("${aipp.secretkey}")
    private String secretKey = "2a4fb82ae84b1e26eb864362e1155fea";
    @Value("${aipp.server}")
    private String SERVER_URL = "http://117.177.222.131:11080";

    @org.junit.Test
    public void test() {
        Order order = new Order();
        order.setId("100000004");
        order.setSupId("cg1705191001220000005");
        order.setProductId("1234567");
        order.setPhone("18811626586");
        order.setNotifyUrl("http://123.56.193.64/hongxin/index");


        long begin = System.currentTimeMillis();

        /*密钥，资源平台方提供*/
        String secretKey = this.secretKey;
        AippPurchaseRequest aippPurchaseRequest = new AippPurchaseRequest();
        /*协议ID*/
        aippPurchaseRequest.setProtocolId(order.getSupId());

		/*订单号*/
        aippPurchaseRequest.setOrderId(order.getId());

		/*订单请求时间*/
        aippPurchaseRequest.setOrderTime(DateUtilsApp.getCurrDateTime("yyyy-MM-dd HH:mm:ss"));

		/*产品ID*/
        aippPurchaseRequest.setProdId(order.getProductId());

		/*订购号码*/
        aippPurchaseRequest.setPhoneNumber(order.getPhone());

		/*回调地址*/
        aippPurchaseRequest.setNotifyUrl(order.getNotifyUrl());

        AippRequest request = new AippRequest();
        try {
            String dataJson = AESUtilApp.encrypt(JSON.toJSONString(aippPurchaseRequest), secretKey);
            request.setPartyId(this.partyId);
            request.setData(dataJson);
            request.setTime(DateUtilsApp.getCurrDateTime("yyyyMMddHHmmssSSS"));
            String sign = MD5UtilApp.getSignAndMD5(request.getPartyId(), request.getData(), request.getTime());
            request.setSign(sign);
        } catch (Exception ex) {
            throw new ServiceException(ResultCode.FAILED);
        }

        /*
        订购
         */
        String resutString = null;
        String failedReason = "发送充值请求失败";
        try {
            String param = JSON.toJSONString(request);
            System.out.println(param);
            resutString = HttpUtilApp.doPost(this.SERVER_URL+"/order/purchase", param);
            System.out.println(resutString);
            if (StringUtils.isEmpty(resutString)) {
                throw new ServiceException(ResultCode.FAILED);
            }
            AippPurchaseResult purchase = JSON.parseObject(resutString, AippPurchaseResult.class);
            if ("1".equals(purchase.getStatus())) {
                order.setRechargeId(purchase.getData().getChannelOrderId());
                //abstractOrderService.setRechargeId(order.getId(), purchase.getData().getChannelOrderId(), "aipp");
            } else {
                failedReason = purchase.getResultDesc();
                throw new ServiceException(ResultCode.FAILED);
            }
        } catch (Exception ex) {
            //rechargeService.rechargeFailed(order.getId(), order.getUsername(), order.getNotifyUrl(),failedReason, order.getExternalId());
        } finally {
             /*
        设置查询参数
         */
            AippQueryRequest aippQueryRequest = new AippQueryRequest();
            //设置订购方订单号
            aippQueryRequest.setOrderId(order.getId());
            //设置资源平台订单号
            aippQueryRequest.setChannelOrderId(order.getRechargeId());
            //AippRequest request = new AippRequest();
            try {
                //请求体json数据
                String dataJson = AESUtilApp.encrypt(JSON.toJSONString(aippQueryRequest), secretKey);
            /*身份ID*/
                request.setPartyId(this.partyId);
            /*data请求体*/
                request.setData(dataJson);
            /*请求时间*/
                request.setTime(DateUtilsApp.getCurrDateTime("yyyyMMddHHmmssSSS"));
            /*签名*/
                //MD5加密
                String md5Str = MD5UtilApp.getSignAndMD5(request.getPartyId(),request.getData(),request.getTime());
                request.setSign(md5Str);
            } catch (Exception ex) {
                throw new ServiceException(ResultCode.FAILED);
            }

            String resultString = null;

            try {

                String param = JSON.toJSONString(request);
                System.out.println(param);
                resultString = HttpUtilApp.doPost(this.SERVER_URL+"/order/query",param);
                System.out.println(resultString);
                if (StringUtils.isEmpty(resultString)) {
                    throw new ServiceException(ResultCode.FAILED);
                }
                AippQueryResult aippQueryResult = JSON.parseObject(resultString, AippQueryResult.class);
            /*请求结果*/
                String status = aippQueryResult.getStatus();
                if ("1".equals(status)) {
                    System.out.println("1");
                } else {
                    System.out.println("0");
                }
            } catch (Exception ex) {
                //logger.warn("查询充值结果失败");
            } finally {
                //logger.info("查询充值结果结束; result:{}; costTime:{}", resultString, (System.currentTimeMillis() - begin));
            }
        }
    }
    public void query(Order order) {

        /*
        设置查询参数
         */
        AippQueryRequest aippQueryRequest = new AippQueryRequest();
        //设置订购方订单号
        aippQueryRequest.setOrderId(order.getId());
        //设置资源平台订单号
        aippQueryRequest.setChannelOrderId(order.getRechargeId());
        AippRequest request = new AippRequest();
        try {
            //请求体json数据
            String dataJson = AESUtilApp.encrypt(JSON.toJSONString(aippQueryRequest), secretKey);
            /*身份ID*/
            request.setPartyId(this.partyId);
            /*data请求体*/
            request.setData(dataJson);
            /*请求时间*/
            request.setTime(DateUtilsApp.getCurrDateTime("yyyyMMddHHmmssSSS"));
            /*签名*/
            //MD5加密
            String md5Str = MD5UtilApp.getSignAndMD5(request.getPartyId(),request.getData(),request.getTime());
            request.setSign(md5Str);
        } catch (Exception ex) {
            throw new ServiceException(ResultCode.FAILED);
        }

        String param = JSON.toJSONString(request);
        String resultString = null;

        try {
            resultString = HttpUtilApp.doPost(this.SERVER_URL+"/order/query",param);
            System.out.println(resultString);
            if (StringUtils.isEmpty(resultString)) {
                throw new ServiceException(ResultCode.FAILED);
            }
            AippQueryResult aippQueryResult = JSON.parseObject(resultString, AippQueryResult.class);
            /*请求结果*/
            String status = aippQueryResult.getStatus();
            if ("1".equals(status)) {
                System.out.println("1");
            } else {
                System.out.println("0");
            }
        } catch (Exception ex) {
            //logger.warn("查询充值结果失败");
        } finally {
            //logger.info("查询充值结果结束; result:{}; costTime:{}", resultString, (System.currentTimeMillis() - begin));
        }
    }

}
