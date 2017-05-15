package com.miaosu.service.huazong.domain;

import com.alibaba.fastjson.annotation.JSONField;

import lombok.Data;

import java.io.Serializable;

/**
 * 返回结果基类
 */
@Data
public class Result implements Serializable{

	private static final long serialVersionUID = -5158644231193497655L;
	
	@JSONField(name = "Code")
    protected String code;
}
