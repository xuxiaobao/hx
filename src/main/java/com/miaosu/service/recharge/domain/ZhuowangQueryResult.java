package com.miaosu.service.recharge.domain;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Data;

@XmlRootElement(name="QueryOrderStatusRsp")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class ZhuowangQueryResult {

	public ZhuowangQueryResult()
	{
	}
	
	@XmlElement(name="TransactionId")
	private String transactionId;
	
	@XmlElement(name="ResultCode")
	private String resultCode;
	
	@XmlElement(name="ResultMsg")
	private String resultMsg;
	
	@XmlElementWrapper(name="OrderStatusList")
	@XmlElement(name="OrderStatusInfo")
	private List<ZhuowangQueryStatus> list;
}
