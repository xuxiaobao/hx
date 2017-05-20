package com.miaosu.service.recharge.domain;

import lombok.Data;

import java.util.List;

/**
 * Created by Administrator on 2017/5/20.
 */
@Data
public class AippNotifyResult {
    private List<Data> dataList;
    public static class Data {
        private String status;
        private String resultCode;
        private String resultDesc;
        private String orderId;
        private String channelOrderId;
        private String phoneNumber;
        private String prodId;
        private String prodName;
        private String channel;
        private String province;
        private String city;
        private String validBeginDate;
        private String validEndDate;
        private String validBeginTime;
        private String validEndTime;
        private String payAmount;
        private String ext;
        private String extDesc;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
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

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
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

        public String getPayAmount() {
            return payAmount;
        }

        public void setPayAmount(String payAmount) {
            this.payAmount = payAmount;
        }

        public String getExt() {
            return ext;
        }

        public void setExt(String ext) {
            this.ext = ext;
        }

        public String getExtDesc() {
            return extDesc;
        }

        public void setExtDesc(String extDesc) {
            this.extDesc = extDesc;
        }
    }
}
