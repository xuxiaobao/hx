package com.miaosu.service.recharge.domain;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Data;

@XmlRootElement(name="OpenCardRsp")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class ZhuowangResult {
	
	public ZhuowangResult()
	{
	}

	@XmlElement(name="TransactionId")
	private String transactionId;
	
	@XmlElement(name="RspCode")
	private String repCode;
	
	@XmlElement(name="ResultMsg")
	private String resultMsg;
	
	@XmlElement(name="IMPPOrderId")
	private String imppOrderId;
}
