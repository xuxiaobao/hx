package com.miaosu.service.recharge.domain;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Data;

@XmlRootElement(name="SynCardStatusReq")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class ZhuowangNotifyResult {

	public ZhuowangNotifyResult()
	{
	}
	
	@XmlElement(name="transactionId")
	private String TransactionId;
	
	@XmlElement(name="channelId")
	private String ChannelId;
	
	@XmlElementWrapper(name="OrderList")
	@XmlElement(name="OrderInfo")
	private List<ZhuowangNotifyStauts> list; 
}
