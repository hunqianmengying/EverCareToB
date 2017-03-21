package com.evercare.app.Entity;

import java.io.Serializable;

/**
 * 作者：xlren on 2016/8/29 13:22
 * 邮箱：renxianliang@126.com
 * TODO
 */

public class Result implements Serializable{
	private static final long serialVersionUID = 6288374846131788743L;

	public static final String SUCCESS = "10200";//成功
	public static final String SIGNATURE_ERROR = "10007";//签名错误
	public static final String FAILED = "failed";
	
	private String status = SUCCESS;
	private String code = "";
	private String msg = "";

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	private String time = "";
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		if(status == null  ||  (!status.equals(SUCCESS) &&  !status.equals(FAILED))){
			throw new IllegalArgumentException("status只允许以下值：" + SUCCESS + "、" + FAILED);
		}
		this.status = status;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
}
