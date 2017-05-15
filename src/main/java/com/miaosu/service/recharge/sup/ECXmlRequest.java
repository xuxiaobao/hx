package com.miaosu.service.recharge.sup;

public class ECXmlRequest {
	

//	<Version>0100</Version>
//	<TestFlag>1</TestFlag>
//	<BIPType>
//		<BIPCode>业务编码,见各接口</BIPCode>
//		<ActivityCode>交易编码,见各接口</ActivityCode>
//		<ActionCode>0</ActionCode>
//	</BIPType>
//	<RoutingInfo>
//		<OrigDomain>DOMS</OrigDomain>
//		<RouteType>00</RouteType>
//		<Routing>
//		<HomeDomain>BBSS</HomeDomain>
//		<RouteValue>998</RouteValue>
//		</Routing>
//	</RoutingInfo>
//	<TransInfo>
//		<SessionID>见附录二</SessionID>
//		<TransIDO>见附录二</TransIDO>
//		<TransIDOTime>20150206164851</TransIDOTime>
//	</TransInfo>
	
	
	String Version;
	String TestFlag;
	BIPTypes BIPType;
	RoutingInfos RoutingInfo;
	TransInfos TransInfo;
	
//	String SvcCont;

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
	
//	public String getSvcCont() {
//		return SvcCont;
//	}
//
//	public void setSvcCont(String svcCont) {
//		SvcCont = svcCont;
//	}





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

}
