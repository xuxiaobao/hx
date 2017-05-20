package com.miaosu.service.recharge.domain;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * Created by Administrator on 2017/5/20.
 */
@Data
public class AippQueryResult {
    @JSONField(name = "status")
    private String status;
    @JSONField(name = "resultCode")
    private String resultCode;
    @JSONField(name = "resultDesc")
    private String resultDesc;
    @JSONField(name = "data")
    private Data data;

    public static class Data {
        private String orderId;
        private String channelOrderId;
        private String orderStatus;
        private String orderTime;
        private String prodId;
        private String prodName;
        private String channel;
        private String province;
        private String city;
        private String payAmount;
        private String phoneNumber;
        private String prodSource;
        private String validBeginDate;
        private String validEndDate;
        private String validBeginTime;
        private String validEndTime;
        private String resultCode;
        private String resultDesc;

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

        public String getOrderStatus() {
            return orderStatus;
        }

        public void setOrderStatus(String orderStatus) {
            this.orderStatus = orderStatus;
        }

        public String getOrderTime() {
            return orderTime;
        }

        public void setOrderTime(String orderTime) {
            this.orderTime = orderTime;
        }

        public String getProdId() {
            return prodId;
        }

        public void setProdId(String prodId) {
            this.prodId = prodId;
        }

        public String getProdName() {
            return prodName;
        }

        public void setProdName(String prodName) {
            this.prodName = prodName;
        }

        public String getChannel() {
            return channel;
        }

        public void setChannel(String channel) {
            this.channel = channel;
        }

        public String getProvince() {
            return province;
        }

        public void setProvince(String province) {
            this.province = province;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getPayAmount() {
            return payAmount;
        }

        public void setPayAmount(String payAmount) {
            this.payAmount = payAmount;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        public String getProdSource() {
            return prodSource;
        }

        public void setProdSource(String prodSource) {
            this.prodSource = prodSource;
        }

        public String getValidBeginDate() {
            return validBeginDate;
        }

        public void setValidBeginDate(String validBeginDate) {
            this.validBeginDate = validBeginDate;
        }

        public String getValidEndDate() {
            return validEndDate;
        }

        public void setValidEndDate(String validEndDate) {
            this.validEndDate = validEndDate;
        }

        public String getValidBeginTime() {
            return validBeginTime;
        }

        public void setValidBeginTime(String validBeginTime) {
            this.validBeginTime = validBeginTime;
        }

        public String getValidEndTime() {
            return validEndTime;
        }

        public void setValidEndTime(String validEndTime) {
            this.validEndTime = validEndTime;
        }

        public String getResultCode() {
            return resultCode;
        }

        public void setResultCode(String resultCode) {
            this.resultCode = resultCode;
        }

        public String getResultDesc() {
            return resultDesc;
        }

        public void setResultDesc(String resultDesc) {
            this.resultDesc = resultDesc;
        }
    }
}
