/*
 * Copyright (C) 2013  Spunk Media Pvt Ltd (www.qubecell.com)
 */

package com.qubecell.beans;

/**
 * This CheckStatusRespBean class is used to hold the response data of CHECK_STATUS_CMD from server.
 * @author Eninov
 *
 */
public class CheckStatusRespBean extends ResponseBaseBean{

	private String message;
	private String requestid;
	private String msisdn;
	private String operator;
	private String txnid;
	private String amount; 
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
	public String getAmount() { 
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
}
