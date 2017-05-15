package com.miaosu.service.recharge.domain;


public class HyfdResult {
	
	int code;
	
	String message;
	
	HyfdData data;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public HyfdData getData() {
		return data;
	}

	public void setData(HyfdData data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "HyfdResult [code=" + code + ", message=" + message + ", data="
				+ data + "]";
	}
}
