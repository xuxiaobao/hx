package com.miaosu.service.recharge.domain;

import com.alibaba.fastjson.annotation.JSONField;

import lombok.Data;

@Data
public class YtkResult {
	
	@JSONField(name = "orderId")
	private String orderId;
	
	@JSONField(name = "respCode")
	private String respCode;
}
