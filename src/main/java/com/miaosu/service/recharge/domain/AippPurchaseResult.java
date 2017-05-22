package com.miaosu.service.recharge.domain;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * Created by Administrator on 2017/5/20.
 */
@Data
public class AippPurchaseResult {
    @JSONField(name = "status")
    private String status;
    @JSONField(name = "resultCode")
    private String resultCode;
    @JSONField(name = "resultDesc")
    private String resultDesc;
    @JSONField(name = "data")
    private Data data;

    public static class Data {
        @JSONField(name = "orderId")
        private String orderId;
        @JSONField(name = "channelOrderId")
        private String channelOrderId;

        public String getOrderId() {
            return orderId;
        }

        public void setOrderId(String orderId) {
            this.orderId = orderId;
        }

        public String getChannelOrderId() {
            return channelOrderId;
        }

        public void setChannelOrderId(String channelOrderId) {
            this.channelOrderId = channelOrderId;
        }
    }

}
