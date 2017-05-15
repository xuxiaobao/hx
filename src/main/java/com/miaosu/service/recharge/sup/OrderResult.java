package com.miaosu.service.recharge.sup;

import com.alibaba.fastjson.annotation.JSONField;

import lombok.Data;

/**
 * 类的描述
 *
 * @author CaoQi
 * @Time 2016/1/9
 */
@Data
public class OrderResult {

    private String status;

    @JSONField(name = "order_id")
    private String orderId;

    private String desc;
}
