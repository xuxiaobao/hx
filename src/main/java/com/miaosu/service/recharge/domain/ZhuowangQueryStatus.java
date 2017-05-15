package com.miaosu.service.recharge.domain;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Data;

@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class ZhuowangQueryStatus {
	
	public ZhuowangQueryStatus()
	{
	}

	@XmlElement(name = "CardOpenTranId")
	private String cardOpenTranId;
	
	@XmlElement(name = "ChannelOrderId")
	private String channelOrderId;
	
	@XmlElement(name = "IMPPOrderId")
	private String iMPPOrderId;
	
	@XmlElement(name = "ChannelId")
	private String channelId;
	
	@XmlElement(name = "Province")
	private String province;
	
	@XmlElement(name = "CardName")
	private String cardName;
	
	@XmlElement(name = "OrderTime")
	private String orderTime;
	
	@XmlElement(name = "OrderStartus")
	private String orderStartus;

	@XmlElement(name = "OrderStatusDesc")
	private String orderStatusDesc;
	
	@XmlElement(name = "OrderFinishTime")
	private String orderFinishTime;
}
