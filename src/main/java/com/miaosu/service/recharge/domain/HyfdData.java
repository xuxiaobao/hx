package com.miaosu.service.recharge.domain;

public class HyfdData {

	String orderId;
	
	String customerOrderId;
	
	String mobile;
	
	String spec;
	
	String basePrice;
	
	String actualPrice;
	
	String status;

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getCustomerOrderId() {
		return customerOrderId;
	}

	public void setCustomerOrderId(String customerOrderId) {
		this.customerOrderId = customerOrderId;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getSpec() {
		return spec;
	}

	public void setSpec(String spec) {
		this.spec = spec;
	}

	public String getBasePrice() {
		return basePrice;
	}

	public void setBasePrice(String basePrice) {
		this.basePrice = basePrice;
	}

	public String getActualPrice() {
		return actualPrice;
	}

	public void setActualPrice(String actualPrice) {
		this.actualPrice = actualPrice;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "HyfdData [orderId=" + orderId + ", customerOrderId="
				+ customerOrderId + ", mobile=" + mobile + ", spec=" + spec
				+ ", basePrice=" + basePrice + ", actualPrice=" + actualPrice
				+ ", status=" + status + "]";
	}
}
