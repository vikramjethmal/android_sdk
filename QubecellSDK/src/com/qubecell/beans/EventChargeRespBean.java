/*
 * Copyright (C) 2013  Spunk Media Pvt Ltd (www.qubecell.com)
 */

package com.qubecell.beans;

/**
 * The EventChargeRespBean class is used to receive eventCharge command response.
 * @author Eninov
 *
 */
public class EventChargeRespBean extends ResponseBaseBean{

	private String message;
	private String requestid;
	private String productid;
	private String msisdn;
	private String operator;
	private String txnid;
	private String amount;
	
	public String getMessage() { 
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getRequestid() { 
		return requestid;
	}
	public String getTxnid() {
		return txnid;
	}
	public void setTxnid(String txnid) {
		this.txnid = txnid;
	}
	public void setRequestid(String requestid) {
		this.requestid = requestid;
	}
	public String getProductid() { 
		return productid;
	}
	public void setProductid(String productid) {
		this.productid = productid;
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
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
}
