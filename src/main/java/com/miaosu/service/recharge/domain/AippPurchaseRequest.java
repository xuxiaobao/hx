package com.miaosu.service.recharge.domain;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * Created by Administrator on 2017/5/19.
 */
@Data
public class AippPurchaseRequest {
    /*采购方建立的采购池协议ID*/
    private String protocolId;

    /*采购方定义的订单号*/
    private String orderId;

    /*订单描述*/
    private String content;

    /*订单请求时间 格式：yyyy-MM-dd HH:mm:ss */
    private String orderTime;

    /*产品ID*/
    private String prodId;

    /*产品区域限制 0：全国，1：限定省，2：限定市*/
    private String areaLimit;

    /*产品渠道限制 0：不限，1：限定渠道*/
    private String channelLimit;

    /*产品渠道  1：移动  2：联通  3：电信*/
    private String channel;

    /*省份编码 详见对接文档省份城市编码表*/
    private String province;

    /*城市编码 详见对接文档省份城市编码表*/
    private String city;

    /*结算金额  资源平台提供该订购类型的结算价格，单位：厘*/
    private String payAmount;

    /*订购号码*/
    private String phoneNumber;

    /*回调地址*/
    private String notifyUrl;

    /*扩展字段 回调时原字段返回*/
    private String ext;
}
