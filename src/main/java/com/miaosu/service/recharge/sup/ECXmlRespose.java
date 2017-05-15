package com.miaosu.service.recharge.sup;

public class ECXmlRespose {
	

//	<?xml version="1.0" encoding="UTF-8"?>
//	<InterBOSS>
//		<Version>0100</Version>
//		<TestFlag>0</TestFlag>
//		<BIPType>
//			<ActionCode>0</ActionCode>
//		</BIPType>
//		<RoutingInfo>
//			<OrigDomain>DOMS</OrigDomain>
//			<RouteType>00</RouteType>
//			<Routing>
//				<HomeDomain>BBSS</HomeDomain>
//				<RouteValue>998</RouteValue>
//			</Routing>
//		</RoutingInfo>
//		<TransInfo>
//			<SessionID>PROC20151021152321000011737052</SessionID>
//			<TransIDO>TEFS20151021152321000011717492</TransIDO>
//			<TransIDOTime>20151021152302</TransIDOTime>
//			<TransIDH>****1445412156007</TransIDH>
//			<TransIDHTime>20151021152236</TransIDHTime>
//		</TransInfo>
//		<Response>
//			<RspType>0</RspType>
//			<RspCode>0000</RspCode>
//			<RspDesc>Success</RspDesc>
//		</Response>
//	</InterBOSS>

	
	
	String Version;
	String TestFlag;
	BIPTypes BIPType;
	RoutingInfos RoutingInfo;
	TransInfos TransInfo;
	
	SvcConts SvcCont;

	public SvcConts getSvcCont() {
		return SvcCont;
	}

	public void setSvcCont(SvcConts svcCont) {
		SvcCont = svcCont;
	}

	public String getVersion() {
		return Version;
	}

	public void setVersion(String version) {
		Version = version;
	}

	public String getTestFlag() {
		return TestFlag;
	}

	public void setTestFlag(String testFlag) {
		TestFlag = testFlag;
	}

	public BIPTypes getBIPType() {
		return BIPType;
	}

	public void setBIPType(BIPTypes bIPType) {
		BIPType = bIPType;
	}
	
	public RoutingInfos getRoutingInfo() {
		return RoutingInfo;
	}

	public void setRoutingInfo(RoutingInfos routingInfo) {
		RoutingInfo = routingInfo;
	}

	public TransInfos getTransInfo() {
		return TransInfo;
	}

	public void setTransInfo(TransInfos transInfo) {
		TransInfo = transInfo;
	}
	
	





	/***
	 * BIPTypes
	 ***/
	public static class BIPTypes{
		String BIPCode;
		String ActivityCode;
		String ActionCode;
		
		
		public BIPTypes() {
			super();
			// TODO Auto-generated constructor stub
		}
		public String getBIPCode() {
			return BIPCode;
		}
		public void setBIPCode(String bIPCode) {
			BIPCode = bIPCode;
		}
		public String getActivityCode() {
			return ActivityCode;
		}
		public void setActivityCode(String activityCode) {
			ActivityCode = activityCode;
		}
		public String getActionCode() {
			return ActionCode;
		}
		public void setActionCode(String actionCode) {
			ActionCode = actionCode;
		}
		
		
	}
	
	
	
	
	
	
	/***
	 * RoutingInfos
	 ***/
	public static class RoutingInfos{
		String OrigDomain;
		String RouteType;
		Routings Routing;
		public String getOrigDomain() {
			return OrigDomain;
		}
		public void setOrigDomain(String origDomain) {
			OrigDomain = origDomain;
		}
		public String getRouteType() {
			return RouteType;
		}
		public void setRouteType(String routeType) {
			RouteType = routeType;
		}
		public Routings getRouting() {
			return Routing;
		}
		public void setRouting(Routings routing) {
			Routing = routing;
		}
		
	}
	
	
	
	
	
	
	/***
	 * Routings
	 ***/
	public static class Routings{
		String HomeDomain;
		String RouteValue;
		public String getHomeDomain() {
			return HomeDomain;
		}
		public void setHomeDomain(String homeDomain) {
			HomeDomain = homeDomain;
		}
		public String getRouteValue() {
			return RouteValue;
		}
		public void setRouteValue(String routeValue) {
			RouteValue = routeValue;
		}
		
	}
	
	
	
	
	
	
	/***
	 * TransInfos
	 ***/
	public static class TransInfos{
		String SessionID;
		String TransIDO;
		String TransIDOTime;
		public String getSessionID() {
			return SessionID;
		}
		public void setSessionID(String sessionID) {
			SessionID = sessionID;
		}
		public String getTransIDO() {
			return TransIDO;
		}
		public void setTransIDO(String transIDO) {
			TransIDO = transIDO;
		}
		public String getTransIDOTime() {
			return TransIDOTime;
		}
		public void setTransIDOTime(String transIDOTime) {
			TransIDOTime = transIDOTime;
		}
		
	}
	
	
	
	
	/**
	 *  SvcConts
	 **/
	public static class SvcConts{
		AdditionRsps AdditionRsp;

		public AdditionRsps getAdditionRsp() {
			return AdditionRsp;
		}

		public void setAdditionRsp(AdditionRsps additionRsp) {
			AdditionRsp = additionRsp;
		}
		
	}
	
	/**
	 *  AdditionRsps
	 **/
	public static class AdditionRsps{
		String Status;
		OperSeqLists OperSeqList;
		String ErrDesc;
		public String getStatus() {
			return Status;
		}
		public void setStatus(String status) {
			Status = status;
		}
		public OperSeqLists getOperSeqList() {
			return OperSeqList;
		}
		public void setOperSeqList(OperSeqLists operSeqList) {
			OperSeqList = operSeqList;
		}
		public String getErrDesc() {
			return ErrDesc;
		}
		public void setErrDesc(String errDesc) {
			ErrDesc = errDesc;
		}
		
	}
	
	
	/**
	 *  OperSeqLists
	 **/
	public static class OperSeqLists{
		String OperSeq;

		public String getOperSeq() {
			return OperSeq;
		}

		public void setOperSeq(String operSeq) {
			OperSeq = operSeq;
		}
	}

}
