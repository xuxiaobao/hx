package com.miaosu.service.recharge.domain;

import com.alibaba.fastjson.annotation.JSONField;

import lombok.Data;

@Data
public class YtkQueryResult {
	
	@JSONField(name = "orderId")
	private String orderId;
	
	@JSONField(name="phoneNo")
	private String phoneNo;
	
	@JSONField(name="pkgNo")
	private String pkgNo;
	
	@JSONField(name = "respCode")
	private String respCode;
}
