/*
 * Copyright (C) 2013  Spunk Media Pvt Ltd (www.qubecell.com)
 */

package com.qubecell.beans;

/**
 * The ValidateOTPRespBean class is used to receive validateOTP command response.
 * @author Eninov
 *
 */
public class ValidateOTPRespBean extends ResponseBaseBean{

	private String message;
	private String requestid;
	private String msisdn;
	private String operator;
	private String txnid;
	private String key;
	
	public String getMessage() { 
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getRequestid() { 
		return requestid;
	}
	public void setRequestid(String requestid) {
		this.requestid = requestid;
	}
	public String getMsisdn() { 
		return msisdn;
	}
	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}
	public String getOperator() {
		return operator;
	}
	public void setOperator(String operator) {
		this.operator = operator;
	}
	public String getTxnid() { 
		return txnid;
	}
	public void setTxnid(String txnid) {
		this.txnid = txnid;
	}
	public String getKey() { 
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
}
