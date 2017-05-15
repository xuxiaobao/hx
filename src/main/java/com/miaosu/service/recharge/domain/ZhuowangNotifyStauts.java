package com.miaosu.service.recharge.domain;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import lombok.Data;

@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class ZhuowangNotifyStauts {
	
	public ZhuowangNotifyStauts()
	{
	}
	
	@XmlElement(name="OrderTransactionId")
	private String orderTransactionId;
	
	@XmlElement(name="ChannelOrderId")
	private String channelOrderId;
	
	@XmlElement(name="ImppOrderId")
	private String imppOrderId;
	
	@XmlElement(name="OrderTime")
	private String orderTime;
	
	@XmlElement(name="OrderStatus")
	private String orderStatus;
	
	@XmlElement(name="FailReason")
	private String failReason;
	
	@XmlElement(name="DetailInfo")
	private String detailInfo;
}
